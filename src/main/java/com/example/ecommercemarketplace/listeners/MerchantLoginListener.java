package com.example.ecommercemarketplace.listeners;

import com.example.ecommercemarketplace.dto.MerchantDto;
import com.example.ecommercemarketplace.events.MerchantLoginEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Async
@Component
public class MerchantLoginListener implements ApplicationListener<MerchantLoginEvent> {

    @Override
    public void onApplicationEvent(MerchantLoginEvent event) {
        MerchantDto merchantDto = event.getMerchantDto();
        log.info("Merchant with publicId={} has logged in.", merchantDto.getPublicId());
    }
}
