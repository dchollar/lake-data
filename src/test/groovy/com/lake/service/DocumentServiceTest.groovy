package com.lake.service

import spock.lang.Specification

class DocumentServiceTest extends Specification {

    def "createRegularExpression"(String phrase, String result) {
        expect:
        DocumentService.createRegularExpression(phrase) == result

        where:
        phrase            | result
        'bob'             | 'BOB'
        null              | null
        ''                | null
        'bob bob'         | 'BOB|BOB'
        '  bob     bob  ' | 'BOB|BOB'
    }

}
