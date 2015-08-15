package com.mattprovis.fluentmatcher;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.AllOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class FluentMatcher<BeanType> extends TypeSafeDiagnosingMatcher<BeanType> {
    protected Map<String, FeatureMatcher<BeanType, ?>> matchers = new TreeMap<>();

    private final Class<BeanType> expectedType;

    public FluentMatcher(Class<BeanType> expectedType) {
        super(expectedType);
        this.expectedType = expectedType;
    }

    @Override
    protected boolean matchesSafely(BeanType item, Description mismatchDescription) {
        return allMatchers().matches(item, mismatchDescription);
    }

    private AllOf<BeanType> allMatchers() {
        List<Matcher<? super BeanType>> allMatchers = new ArrayList<>();
        for (FeatureMatcher<BeanType, ?> fieldMatcher : matchers.values()) {
            allMatchers.add(fieldMatcher);
        }
        return new AllOf<>(allMatchers);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("(a " + expectedType.getSimpleName() + " with: ");
        allMatchers().describeTo(description);
        description.appendText(")");
    }

    protected <FieldType> void registerFieldMatcher(final String fieldName, final Matcher<FieldType> messageMatcher) {
        matchers.put(fieldName, new FeatureMatcher<BeanType, FieldType>(messageMatcher, fieldName, fieldName) {
            @Override
            protected FieldType featureValueOf(BeanType actual) {
                try {
                    return (FieldType) FieldUtils.readDeclaredField(actual, fieldName, true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
