package com.example.cdn.step

import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.FieldSet

class CloudFrontLogFieldSetMapper : FieldSetMapper<CloudFrontLogRecord> {
    override fun mapFieldSet(fieldSet: FieldSet) = CloudFrontLogRecord(
        date = fieldSet.readString("date"),
        time = fieldSet.readString("time"),
        edgeLocation = fieldSet.readString("edgeLocation"),
        bytes = fieldSet.readString("bytes"),
        clientIp = fieldSet.readString("clientIp"),
        method = fieldSet.readString("method"),
        host = fieldSet.readString("host"),
        uriStem = fieldSet.readString("uriStem"),
        status = fieldSet.readString("status"),
        userAgent = fieldSet.readString("userAgent"),
        resultType = fieldSet.readString("resultType"),
        protocol = fieldSet.readString("protocol"),
    )
}
