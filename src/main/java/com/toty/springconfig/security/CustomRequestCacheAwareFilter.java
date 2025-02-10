package com.toty.springconfig.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.stereotype.Component;

//@Component
//public class CustomRequestCacheAwareFilter extends RequestCacheAwareFilter {
//
//    public CustomRequestCacheAwareFilter(CookieRequestCache cookieRequestCache) {
//        super(cookieRequestCache);
//    }
//}
