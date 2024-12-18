package com.zwz.common.gray.balancer;

import com.alibaba.cloud.nacos.balancer.NacosBalancer;
import com.zwz.common.gray.keeper.GrayTagKeeper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * 标签选择工具
 * @author weizhen.zhang
 * @date 2024/12/10 21:00
 */
@Slf4j
public class GrayLoadBalancerUtil {

    public static Mono<Response<ServiceInstance>> choose(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, Request request) {
        // 获得 HttpHeaders 属性，实现从 header 中获取
        HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();
        // 选择实例
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(list -> getInstanceResponse(serviceId, list, headers));
    }

    private static Response<ServiceInstance> getInstanceResponse(String serviceId, List<ServiceInstance> instances, HttpHeaders headers) {
        // 如果服务实例为空，则直接返回
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("[ 服务实例列表为空 : 服务({})]", serviceId);
            return new EmptyResponse();
        }

        List<ServiceInstance> chooseInstances = filterTagServiceInstances(serviceId, instances, headers);

        // 随机 + 权重获取实例列表
        return new DefaultResponse(NacosBalancer.getHostByRandomWeight3(chooseInstances));
    }


    /**
     * 基于 tag 请求头，过滤匹配 tag 的服务实例列表
     *
     * copy from EnvLoadBalancerClient
     *
     * @param instances 服务实例列表
     * @param headers   请求头
     * @return 服务实例列表
     */
    private static List<ServiceInstance> filterTagServiceInstances(String serviceId, List<ServiceInstance> instances, HttpHeaders headers) {
        // 情况一，没有标签时，直接返回没有标签的服务列表
        String tag = GrayTagKeeper.getGrayTag();
        log.debug("从上下文中获取grayTag:"+tag);
        if (StringUtils.isEmpty(tag)) {
            if (CollectionUtils.isNotEmpty(headers.get(GrayTagKeeper.GRAY_TAG))) {
                tag = headers.get(GrayTagKeeper.GRAY_TAG).get(0);
                log.debug("从header中获取grayTag:"+tag);
            }
        }
        if (StringUtils.isEmpty(tag)) {
            return instances.stream().filter(instance -> StringUtils.isBlank(getTagFromMeta(instance))).collect(Collectors.toList());
        }

        // 情况二，有 tag 时，使用 tag 匹配服务实例
        String finalTag = tag;
        List<ServiceInstance> chooseInstances = filterList(instances, instance -> finalTag.equals(getTagFromMeta(instance)));
        if (CollectionUtils.isEmpty(chooseInstances)) {
            log.warn("[serviceId({}) 没有满足 tag({}) 的服务实例列表，直接使用所有服务实例列表]", serviceId, tag);
            chooseInstances = instances.stream().filter(instance -> StringUtils.isBlank(getTagFromMeta(instance))).collect(Collectors.toList());
        }
        return chooseInstances;
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>(10);
        }
        return from.stream().filter(predicate).collect(Collectors.toList());
    }

    private static String getTagFromMeta(ServiceInstance instance) {
        return instance.getMetadata().get("graytag");
    }
}
