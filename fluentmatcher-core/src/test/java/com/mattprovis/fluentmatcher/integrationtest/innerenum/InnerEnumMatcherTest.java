package com.mattprovis.fluentmatcher.integrationtest.innerenum;

import com.mattprovis.fluentmatcher.FluentMatcher;
import org.junit.Test;

import static com.mattprovis.fluentmatcher.integrationtest.GeneratedMatcherTestUtils.createMatcherFor;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InnerEnumMatcherTest {

    @Test
    public void canCompileAndExecuteGeneratedFluentMatcherForClassThatHasAnInnerEnum() throws Exception {
        FluentMatcher matcherInstance = createMatcherFor(ClassWithEnum.class);

        ClassWithEnum matchingInstance = new ClassWithEnum(ClassWithEnum.Colour.BLUE);
        assertThat(matcherInstance.matches(matchingInstance), is(true));
    }
}
