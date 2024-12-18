package com.zwz.common.gray.balancer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

/**
 * 网关灰度负载客户端筛选
 * @author weizhen.zhang
 * @date 2024/12/10 21:11
 */
@Slf4j
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {


    /**
     * 用于获取 serviceId 对应的服务实例的列表
     */
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * 需要获取的服务实例名
     *
     * 暂时用于打印 logger 日志
     */
    private final String serviceId;

    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> supplierObjectProvider, String serviceId) {
        this.serviceInstanceListSupplierProvider = supplierObjectProvider;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 选择实例
        return GrayLoadBalancerUtil.choose(serviceInstanceListSupplierProvider, serviceId, request);
    }
}
