package io.invok.core.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeHintsConfig.InvokRuntimeHints.class)
public class NativeHintsConfig {

        public static class InvokRuntimeHints implements RuntimeHintsRegistrar {

                @Override
                public void registerHints(@org.springframework.lang.NonNull RuntimeHints hints, @org.springframework.lang.Nullable ClassLoader classLoader) {
                        // Jasypt hints
                        hints.reflection().registerTypeIfPresent(classLoader,
                                        "org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource",
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                        MemberCategory.INVOKE_DECLARED_METHODS);

                        hints.reflection().registerTypeIfPresent(classLoader,
                                        "com.ulisesbocchio.jasyptspringboot.EncryptablePropertySourceConverter",
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                        MemberCategory.INVOKE_DECLARED_METHODS);

                        // SQLite JDBC hints
                        hints.reflection().registerTypeIfPresent(classLoader, "org.sqlite.JDBC",
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                        MemberCategory.INVOKE_DECLARED_METHODS);

                        // Hibernate SQLite Dialect
                        hints.reflection().registerTypeIfPresent(classLoader,
                                        "org.hibernate.community.dialect.SQLiteDialect",
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                        MemberCategory.INVOKE_DECLARED_METHODS);
                }
        }
}
