package com.giyong.cdnloganalytics.domain.cdn;

import com.giyong.cdnloganalytics.common.CdnProvider;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryBasedCdnProviderResolverTest {

    @Test
    void should_return_aws_when_aws_path() {
        //given
        DirectoryBasedCdnProviderResolver cdnProviderResolver = new DirectoryBasedCdnProviderResolver();
        Path path = Path.of("/logs/aws/file.log");

        //when
        CdnProvider result = cdnProviderResolver.resolve(path);

        //then
        assertEquals(CdnProvider.AWS, result);
    }

    @Test
    void should_return_akamai_when_akamai_path() {
        //given
        DirectoryBasedCdnProviderResolver cdnProviderResolver = new DirectoryBasedCdnProviderResolver();
        Path path = Path.of("/logs/akamai/file.log");

        //when
        CdnProvider result = cdnProviderResolver.resolve(path);

        //then
        assertEquals(CdnProvider.AKAMAI, result);
    }

}