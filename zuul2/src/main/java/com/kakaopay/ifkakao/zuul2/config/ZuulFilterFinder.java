package com.kakaopay.ifkakao.zuul2.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.reflect.ClassPath;
import com.google.inject.Provides;
import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.filters.ZuulFilter;
import com.netflix.zuul.init.ZuulFiltersModule;
import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author hubert.squid
 * @since 2020.07.28
 */
public class ZuulFilterFinder {

    private static final Logger LOG = LoggerFactory.getLogger(ZuulFiltersModule.class);
    private static final Predicate<String> blank = String::isEmpty;

    @Provides
    FilterFileManager.FilterFileManagerConfig newFilterFileManagerConfig() {
        // Get filter directories.
        final AbstractConfiguration config = ConfigurationManager.getConfigInstance();

        String[] filterLocations = findFilterLocations(config);
        String[] filterClassNames = findClassNames(config);

        // Init the FilterStore.
        return new FilterFileManager.FilterFileManagerConfig(filterLocations, filterClassNames, 5);
    }

    // Get compiled filter classes to be found on classpath.
    @VisibleForTesting
    String[] findClassNames(AbstractConfiguration config) {

        // Find individually-specified filter classes.
        String[] filterClassNamesStrArray = config.getStringArray("zuul.filters.classes");
        Stream<String> classNameStream = Arrays.stream(filterClassNamesStrArray)
            .map(String::trim)
            .filter(blank.negate());

        // Find filter classes in specified packages.
        String[] packageNamesStrArray = config.getStringArray("zuul.filters.packages");
        ClassPath cp;
        try {
            cp = ClassPath.from(this.getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Error attempting to read classpath to find filters!", e);
        }

        Stream<String> packageStream = Arrays.stream(packageNamesStrArray)
            .map(String::trim)
            .filter(blank.negate())
            .flatMap(packageName -> cp.getTopLevelClasses(packageName).stream())
            .map(ClassPath.ClassInfo::load)
            .filter(ZuulFilter.class::isAssignableFrom)
            .map(Class::getCanonicalName);


        String[] filterClassNames = Stream.concat(classNameStream, packageStream).toArray(String[]::new);
        if (filterClassNames.length != 0) {
            LOG.info("Using filter classnames: ");
            for (String location : filterClassNames) {
                LOG.info("  " + location);
            }
        }

        return filterClassNames;
    }

    @VisibleForTesting
    String[] findFilterLocations(AbstractConfiguration config) {
        String[] locations = config.getStringArray("zuul.filters.locations");
        if (locations == null) {
            locations = new String[]{"inbound", "outbound", "endpoint"};
        }
        String[] filterLocations = Arrays.stream(locations)
            .map(String::trim)
            .filter(blank.negate())
            .toArray(String[]::new);

        if (filterLocations.length != 0) {
            LOG.info("Using filter locations: ");
            for (String location : filterLocations) {
                LOG.info("  " + location);
            }
        }
        return filterLocations;
    }
}
