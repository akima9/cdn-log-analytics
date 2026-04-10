package com.giyong.cdnloganalytics.domain.cdn;

import com.giyong.cdnloganalytics.common.CdnProvider;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DirectoryBasedCdnProviderResolver implements CdnProviderResolver{

    @Override
    public CdnProvider resolve(Path path) {
        for (Path part : path) {
            if (part.toString().equalsIgnoreCase("aws")) {
                return CdnProvider.AWS;
            }
            if (part.toString().equalsIgnoreCase("akamai")) {
                return CdnProvider.AKAMAI;
            }
            if (part.toString().equalsIgnoreCase("tencent")) {
                return CdnProvider.TENCENT;
            }
        }

        throw new IllegalArgumentException("Unknown CDN: " + path);
    }
}
