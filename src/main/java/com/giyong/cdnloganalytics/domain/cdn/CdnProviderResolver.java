package com.giyong.cdnloganalytics.domain.cdn;

import com.giyong.cdnloganalytics.common.CdnProvider;

import java.nio.file.Path;

public interface CdnProviderResolver {
    CdnProvider resolve(Path path);
}
