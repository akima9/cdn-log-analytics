package com.giyong.cdnloganalytics.parser;

import java.util.Map;

class AwsLogParserTest extends LogParserTest{

    @Override
    protected LogParser createLogParser() {
        return new AwsLogParser();
    }

    @Override
    protected String validLogLine() {
        return "2026-01-10\t10:15:21\tICN1\t512\t203.0.113.10\tGET\td111111abcdef8.cloudfront.net\t/live/stream.m3u8\t200\t-\tMozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)\tchannel_id=1001&program_id=55501\t-\tHit\tAbCdEf1234567890\td111111abcdef8.cloudfront.net\thttps\t34\t0.002\t-\tTLSv1.2\tECDHE-RSA-AES128-GCM-SHA256\tHit\tHTTP/2.0\t-\t-\t50432\t0.002\tHit\tapplication/vnd.apple.mpegurl\t512\t-\t-";
    }

    @Override
    protected Map<String, Integer> fieldIndex() {
        return AwsFieldParser.parse("#Fields: date time x-edge-location sc-bytes c-ip cs-method cs(Host) cs-uri-stem sc-status cs(Referer) cs(User-Agent) cs-uri-query cs(Cookie) x-edge-result-type x-edge-request-id x-host-header cs-protocol cs-bytes time-taken x-forwarded-for ssl-protocol ssl-cipher x-edge-response-result-type cs-protocol-version fle-status fle-encrypted-fields c-port time-to-first-byte x-edge-detailed-result-type sc-content-type sc-content-len sc-range-start sc-range-end");
    }
}