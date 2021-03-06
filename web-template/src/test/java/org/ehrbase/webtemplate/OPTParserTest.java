/*
 *
 *  *  Copyright (c) 2020  Stefan Spiska (Vitasystems GmbH) and Hannover Medical School
 *  *  This file is part of Project EHRbase
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.ehrbase.webtemplate;

import org.apache.xmlbeans.XmlException;
import org.ehrbase.test_data.operationaltemplate.OperationalTemplateTestData;
import org.junit.jupiter.api.Test;
import org.openehr.schemas.v1.OPERATIONALTEMPLATE;
import org.openehr.schemas.v1.TemplateDocument;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class OPTParserTest {

    @Test
    void parse() throws IOException, XmlException {
        OPERATIONALTEMPLATE template = TemplateDocument.Factory.parse(OperationalTemplateTestData.CORONA_ANAMMNESE.getStream()).getTemplate();

        OPTParser cut = new OPTParser(template);
        WebTemplate actual = cut.parse();
        assertThat(actual).isNotNull();
        assertThat(actual.getTree().getChildren()).size().isEqualTo(6);
    }

    @Test
    void testFindByAqlPath() throws IOException, XmlException {
        OPERATIONALTEMPLATE template = TemplateDocument.Factory.parse(OperationalTemplateTestData.CORONA_ANAMMNESE.getStream()).getTemplate();

        OPTParser cut = new OPTParser(template);
        WebTemplate actual = cut.parse();

        assertThat(actual.findByAqlPath("/content[openEHR-EHR-SECTION.adhoc.v1 and name/value='Symptome']").isPresent()).isTrue();
        assertThat(actual.findByAqlPath("/content[openEHR-EHR-SECTION.adhoc.v1]").isPresent()).isTrue();
    }

    @Test
    void testQueryUpperUnbounded() throws IOException, XmlException {
        OPERATIONALTEMPLATE operationaltemplate = TemplateDocument.Factory.parse(OperationalTemplateTestData.IDCR_PROBLEM_LIST.getStream()).getTemplate();
        List<WebTemplateNode> result = new OPTParser(operationaltemplate).parse().upperNotBounded();

        assertNotNull(result);

        assertEquals(3, result.size());
    }

    @Test
    void testQueryUpperUnbounded2() throws IOException, XmlException {
        OPERATIONALTEMPLATE operationaltemplate = TemplateDocument.Factory.parse(OperationalTemplateTestData.IDCR_LABORATORY_TEST.getStream()).getTemplate();
        List<WebTemplateNode> result = new OPTParser(operationaltemplate).parse().upperNotBounded();

        assertNotNull(result);

        assertEquals(42, result.size());
    }

    @Test
    void testMultiValued() throws IOException, XmlException {
        OPERATIONALTEMPLATE operationaltemplate = TemplateDocument.Factory.parse(OperationalTemplateTestData.CORONA_ANAMMNESE.getStream()).getTemplate();
        List<WebTemplateNode> result = new OPTParser(operationaltemplate).parse().multiValued();

        assertNotNull(result);

        assertEquals(42, result.size());
    }


    @Test
    void findAllContainmentCombinations() throws IOException, XmlException {
        OPERATIONALTEMPLATE operationaltemplate = TemplateDocument.Factory.parse(OperationalTemplateTestData.BLOOD_PRESSURE_SIMPLE.getStream()).getTemplate();
        Set<Set<NodeId>> actual = new OPTParser(operationaltemplate).parse().findAllContainmentCombinations();

        assertThat(actual).size().isEqualTo(5);
    }
}