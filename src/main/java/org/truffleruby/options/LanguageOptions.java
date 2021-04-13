/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.options;

// GENERATED BY tool/generate-options.rb
// This file is automatically generated from options.yml with 'jt build options'

import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionValues;
import org.truffleruby.shared.options.OptionsCatalog;
import com.oracle.truffle.api.TruffleLogger;

import com.oracle.truffle.api.TruffleLanguage.Env;

// @formatter:off
public class LanguageOptions {

    /** --core-load-path="resource:/truffleruby" */
    public final String CORE_LOAD_PATH;
    /** --frozen-string-literals=false */
    public final boolean FROZEN_STRING_LITERALS;
    /** --lazy-default=true */
    public final boolean DEFAULT_LAZY;
    /** --lazy-calltargets=singleContext && DEFAULT_LAZY */
    public final boolean LAZY_CALLTARGETS;
    /** --core-as-internal=false */
    public final boolean CORE_AS_INTERNAL;
    /** --stdlib-as-internal=false */
    public final boolean STDLIB_AS_INTERNAL;
    /** --lazy-translation-user=LAZY_CALLTARGETS */
    public final boolean LAZY_TRANSLATION_USER;
    /** --backtraces-omit-unused=true */
    public final boolean BACKTRACES_OMIT_UNUSED;
    /** --lazy-translation-log=false */
    public final boolean LAZY_TRANSLATION_LOG;
    /** --constant-dynamic-lookup-log=false */
    public final boolean LOG_DYNAMIC_CONSTANT_LOOKUP;
    /** --lazy-builtins=LAZY_CALLTARGETS */
    public final boolean LAZY_BUILTINS;
    /** --lazy-translation-core=LAZY_CALLTARGETS */
    public final boolean LAZY_TRANSLATION_CORE;
    /** --basic-ops-inline=true */
    public final boolean BASICOPS_INLINE;
    /** --profile-arguments=true */
    public final boolean PROFILE_ARGUMENTS;
    /** --default-cache=8 */
    public final int DEFAULT_CACHE;
    /** --method-lookup-cache=DEFAULT_CACHE */
    public final int METHOD_LOOKUP_CACHE;
    /** --dispatch-cache=DEFAULT_CACHE */
    public final int DISPATCH_CACHE;
    /** --yield-cache=DEFAULT_CACHE */
    public final int YIELD_CACHE;
    /** --to-proc-cache=DEFAULT_CACHE */
    public final int METHOD_TO_PROC_CACHE;
    /** --is-a-cache=DEFAULT_CACHE */
    public final int IS_A_CACHE;
    /** --bind-cache=DEFAULT_CACHE */
    public final int BIND_CACHE;
    /** --constant-cache=DEFAULT_CACHE */
    public final int CONSTANT_CACHE;
    /** --instance-variable-cache=DEFAULT_CACHE */
    public final int INSTANCE_VARIABLE_CACHE;
    /** --binding-local-variable-cache=DEFAULT_CACHE */
    public final int BINDING_LOCAL_VARIABLE_CACHE;
    /** --symbol-to-proc-cache=DEFAULT_CACHE */
    public final int SYMBOL_TO_PROC_CACHE;
    /** --pack-cache=DEFAULT_CACHE */
    public final int PACK_CACHE;
    /** --unpack-cache=DEFAULT_CACHE */
    public final int UNPACK_CACHE;
    /** --eval-cache=DEFAULT_CACHE */
    public final int EVAL_CACHE;
    /** --encoding-compatible-query-cache=DEFAULT_CACHE */
    public final int ENCODING_COMPATIBLE_QUERY_CACHE;
    /** --encoding-loaded-classes-cache=DEFAULT_CACHE */
    public final int ENCODING_LOADED_CLASSES_CACHE;
    /** --interop-convert-cache=DEFAULT_CACHE */
    public final int INTEROP_CONVERT_CACHE;
    /** --time-format-cache=DEFAULT_CACHE */
    public final int TIME_FORMAT_CACHE;
    /** --integer-pow-cache=DEFAULT_CACHE */
    public final int POW_CACHE;
    /** --ruby-library-cache=DEFAULT_CACHE */
    public final int RUBY_LIBRARY_CACHE;
    /** --thread-cache=!singleContext ? 0 : 1 */
    public final int THREAD_CACHE;
    /** --context-identity-cache=1 */
    public final int CONTEXT_SPECIFIC_IDENTITY_CACHE;
    /** --identity-cache=1 */
    public final int IDENTITY_CACHE;
    /** --class-cache=3 */
    public final int CLASS_CACHE;
    /** --array-dup-cache=3 */
    public final int ARRAY_DUP_CACHE;
    /** --array-strategy-cache=4 */
    public final int ARRAY_STRATEGY_CACHE;
    /** --array-uninitialized-size=16 */
    public final int ARRAY_UNINITIALIZED_SIZE;
    /** --hash-packed-array-max=3 */
    public final int HASH_PACKED_ARRAY_MAX;
    /** --pack-unroll=4 */
    public final int PACK_UNROLL_LIMIT;
    /** --pack-recover=32 */
    public final int PACK_RECOVER_LOOP_MIN;
    /** --regexp-instrument-creation=false */
    public final boolean REGEXP_INSTRUMENT_CREATION;
    /** --shared-objects=true */
    public final boolean SHARED_OBJECTS_ENABLED;
    /** --shared-objects-debug=false */
    public final boolean SHARED_OBJECTS_DEBUG;
    /** --shared-objects-force=false */
    public final boolean SHARED_OBJECTS_FORCE;

    public LanguageOptions(Env env, OptionValues options, boolean singleContext) {
        CORE_LOAD_PATH = options.get(OptionsCatalog.CORE_LOAD_PATH_KEY);
        FROZEN_STRING_LITERALS = options.get(OptionsCatalog.FROZEN_STRING_LITERALS_KEY);
        DEFAULT_LAZY = options.get(OptionsCatalog.DEFAULT_LAZY_KEY);
        LAZY_CALLTARGETS = singleContext && (options.hasBeenSet(OptionsCatalog.LAZY_CALLTARGETS_KEY) ? options.get(OptionsCatalog.LAZY_CALLTARGETS_KEY) : DEFAULT_LAZY);
        CORE_AS_INTERNAL = options.get(OptionsCatalog.CORE_AS_INTERNAL_KEY);
        STDLIB_AS_INTERNAL = options.get(OptionsCatalog.STDLIB_AS_INTERNAL_KEY);
        LAZY_TRANSLATION_USER = options.hasBeenSet(OptionsCatalog.LAZY_TRANSLATION_USER_KEY) ? options.get(OptionsCatalog.LAZY_TRANSLATION_USER_KEY) : LAZY_CALLTARGETS;
        BACKTRACES_OMIT_UNUSED = options.get(OptionsCatalog.BACKTRACES_OMIT_UNUSED_KEY);
        LAZY_TRANSLATION_LOG = options.get(OptionsCatalog.LAZY_TRANSLATION_LOG_KEY);
        LOG_DYNAMIC_CONSTANT_LOOKUP = options.get(OptionsCatalog.LOG_DYNAMIC_CONSTANT_LOOKUP_KEY);
        LAZY_BUILTINS = options.hasBeenSet(OptionsCatalog.LAZY_BUILTINS_KEY) ? options.get(OptionsCatalog.LAZY_BUILTINS_KEY) : LAZY_CALLTARGETS;
        LAZY_TRANSLATION_CORE = options.hasBeenSet(OptionsCatalog.LAZY_TRANSLATION_CORE_KEY) ? options.get(OptionsCatalog.LAZY_TRANSLATION_CORE_KEY) : LAZY_CALLTARGETS;
        BASICOPS_INLINE = options.get(OptionsCatalog.BASICOPS_INLINE_KEY);
        PROFILE_ARGUMENTS = options.get(OptionsCatalog.PROFILE_ARGUMENTS_KEY);
        DEFAULT_CACHE = options.get(OptionsCatalog.DEFAULT_CACHE_KEY);
        METHOD_LOOKUP_CACHE = options.hasBeenSet(OptionsCatalog.METHOD_LOOKUP_CACHE_KEY) ? options.get(OptionsCatalog.METHOD_LOOKUP_CACHE_KEY) : DEFAULT_CACHE;
        DISPATCH_CACHE = options.hasBeenSet(OptionsCatalog.DISPATCH_CACHE_KEY) ? options.get(OptionsCatalog.DISPATCH_CACHE_KEY) : DEFAULT_CACHE;
        YIELD_CACHE = options.hasBeenSet(OptionsCatalog.YIELD_CACHE_KEY) ? options.get(OptionsCatalog.YIELD_CACHE_KEY) : DEFAULT_CACHE;
        METHOD_TO_PROC_CACHE = options.hasBeenSet(OptionsCatalog.METHOD_TO_PROC_CACHE_KEY) ? options.get(OptionsCatalog.METHOD_TO_PROC_CACHE_KEY) : DEFAULT_CACHE;
        IS_A_CACHE = options.hasBeenSet(OptionsCatalog.IS_A_CACHE_KEY) ? options.get(OptionsCatalog.IS_A_CACHE_KEY) : DEFAULT_CACHE;
        BIND_CACHE = options.hasBeenSet(OptionsCatalog.BIND_CACHE_KEY) ? options.get(OptionsCatalog.BIND_CACHE_KEY) : DEFAULT_CACHE;
        CONSTANT_CACHE = options.hasBeenSet(OptionsCatalog.CONSTANT_CACHE_KEY) ? options.get(OptionsCatalog.CONSTANT_CACHE_KEY) : DEFAULT_CACHE;
        INSTANCE_VARIABLE_CACHE = options.hasBeenSet(OptionsCatalog.INSTANCE_VARIABLE_CACHE_KEY) ? options.get(OptionsCatalog.INSTANCE_VARIABLE_CACHE_KEY) : DEFAULT_CACHE;
        BINDING_LOCAL_VARIABLE_CACHE = options.hasBeenSet(OptionsCatalog.BINDING_LOCAL_VARIABLE_CACHE_KEY) ? options.get(OptionsCatalog.BINDING_LOCAL_VARIABLE_CACHE_KEY) : DEFAULT_CACHE;
        SYMBOL_TO_PROC_CACHE = options.hasBeenSet(OptionsCatalog.SYMBOL_TO_PROC_CACHE_KEY) ? options.get(OptionsCatalog.SYMBOL_TO_PROC_CACHE_KEY) : DEFAULT_CACHE;
        PACK_CACHE = options.hasBeenSet(OptionsCatalog.PACK_CACHE_KEY) ? options.get(OptionsCatalog.PACK_CACHE_KEY) : DEFAULT_CACHE;
        UNPACK_CACHE = options.hasBeenSet(OptionsCatalog.UNPACK_CACHE_KEY) ? options.get(OptionsCatalog.UNPACK_CACHE_KEY) : DEFAULT_CACHE;
        EVAL_CACHE = options.hasBeenSet(OptionsCatalog.EVAL_CACHE_KEY) ? options.get(OptionsCatalog.EVAL_CACHE_KEY) : DEFAULT_CACHE;
        ENCODING_COMPATIBLE_QUERY_CACHE = options.hasBeenSet(OptionsCatalog.ENCODING_COMPATIBLE_QUERY_CACHE_KEY) ? options.get(OptionsCatalog.ENCODING_COMPATIBLE_QUERY_CACHE_KEY) : DEFAULT_CACHE;
        ENCODING_LOADED_CLASSES_CACHE = options.hasBeenSet(OptionsCatalog.ENCODING_LOADED_CLASSES_CACHE_KEY) ? options.get(OptionsCatalog.ENCODING_LOADED_CLASSES_CACHE_KEY) : DEFAULT_CACHE;
        INTEROP_CONVERT_CACHE = options.hasBeenSet(OptionsCatalog.INTEROP_CONVERT_CACHE_KEY) ? options.get(OptionsCatalog.INTEROP_CONVERT_CACHE_KEY) : DEFAULT_CACHE;
        TIME_FORMAT_CACHE = options.hasBeenSet(OptionsCatalog.TIME_FORMAT_CACHE_KEY) ? options.get(OptionsCatalog.TIME_FORMAT_CACHE_KEY) : DEFAULT_CACHE;
        POW_CACHE = options.hasBeenSet(OptionsCatalog.POW_CACHE_KEY) ? options.get(OptionsCatalog.POW_CACHE_KEY) : DEFAULT_CACHE;
        RUBY_LIBRARY_CACHE = options.hasBeenSet(OptionsCatalog.RUBY_LIBRARY_CACHE_KEY) ? options.get(OptionsCatalog.RUBY_LIBRARY_CACHE_KEY) : DEFAULT_CACHE;
        THREAD_CACHE = !singleContext ? 0 : (options.get(OptionsCatalog.THREAD_CACHE_KEY));
        CONTEXT_SPECIFIC_IDENTITY_CACHE = options.get(OptionsCatalog.CONTEXT_SPECIFIC_IDENTITY_CACHE_KEY);
        IDENTITY_CACHE = options.get(OptionsCatalog.IDENTITY_CACHE_KEY);
        CLASS_CACHE = options.get(OptionsCatalog.CLASS_CACHE_KEY);
        ARRAY_DUP_CACHE = options.get(OptionsCatalog.ARRAY_DUP_CACHE_KEY);
        ARRAY_STRATEGY_CACHE = options.get(OptionsCatalog.ARRAY_STRATEGY_CACHE_KEY);
        ARRAY_UNINITIALIZED_SIZE = options.get(OptionsCatalog.ARRAY_UNINITIALIZED_SIZE_KEY);
        HASH_PACKED_ARRAY_MAX = options.get(OptionsCatalog.HASH_PACKED_ARRAY_MAX_KEY);
        PACK_UNROLL_LIMIT = options.get(OptionsCatalog.PACK_UNROLL_LIMIT_KEY);
        PACK_RECOVER_LOOP_MIN = options.get(OptionsCatalog.PACK_RECOVER_LOOP_MIN_KEY);
        REGEXP_INSTRUMENT_CREATION = options.get(OptionsCatalog.REGEXP_INSTRUMENT_CREATION_KEY);
        SHARED_OBJECTS_ENABLED = options.get(OptionsCatalog.SHARED_OBJECTS_ENABLED_KEY);
        SHARED_OBJECTS_DEBUG = options.get(OptionsCatalog.SHARED_OBJECTS_DEBUG_KEY);
        SHARED_OBJECTS_FORCE = options.get(OptionsCatalog.SHARED_OBJECTS_FORCE_KEY);
    }

    public Object fromDescriptor(OptionDescriptor descriptor) {
        switch (descriptor.getName()) {
            case "ruby.core-load-path":
                return CORE_LOAD_PATH;
            case "ruby.frozen-string-literals":
                return FROZEN_STRING_LITERALS;
            case "ruby.lazy-default":
                return DEFAULT_LAZY;
            case "ruby.lazy-calltargets":
                return LAZY_CALLTARGETS;
            case "ruby.core-as-internal":
                return CORE_AS_INTERNAL;
            case "ruby.stdlib-as-internal":
                return STDLIB_AS_INTERNAL;
            case "ruby.lazy-translation-user":
                return LAZY_TRANSLATION_USER;
            case "ruby.backtraces-omit-unused":
                return BACKTRACES_OMIT_UNUSED;
            case "ruby.lazy-translation-log":
                return LAZY_TRANSLATION_LOG;
            case "ruby.constant-dynamic-lookup-log":
                return LOG_DYNAMIC_CONSTANT_LOOKUP;
            case "ruby.lazy-builtins":
                return LAZY_BUILTINS;
            case "ruby.lazy-translation-core":
                return LAZY_TRANSLATION_CORE;
            case "ruby.basic-ops-inline":
                return BASICOPS_INLINE;
            case "ruby.profile-arguments":
                return PROFILE_ARGUMENTS;
            case "ruby.default-cache":
                return DEFAULT_CACHE;
            case "ruby.method-lookup-cache":
                return METHOD_LOOKUP_CACHE;
            case "ruby.dispatch-cache":
                return DISPATCH_CACHE;
            case "ruby.yield-cache":
                return YIELD_CACHE;
            case "ruby.to-proc-cache":
                return METHOD_TO_PROC_CACHE;
            case "ruby.is-a-cache":
                return IS_A_CACHE;
            case "ruby.bind-cache":
                return BIND_CACHE;
            case "ruby.constant-cache":
                return CONSTANT_CACHE;
            case "ruby.instance-variable-cache":
                return INSTANCE_VARIABLE_CACHE;
            case "ruby.binding-local-variable-cache":
                return BINDING_LOCAL_VARIABLE_CACHE;
            case "ruby.symbol-to-proc-cache":
                return SYMBOL_TO_PROC_CACHE;
            case "ruby.pack-cache":
                return PACK_CACHE;
            case "ruby.unpack-cache":
                return UNPACK_CACHE;
            case "ruby.eval-cache":
                return EVAL_CACHE;
            case "ruby.encoding-compatible-query-cache":
                return ENCODING_COMPATIBLE_QUERY_CACHE;
            case "ruby.encoding-loaded-classes-cache":
                return ENCODING_LOADED_CLASSES_CACHE;
            case "ruby.interop-convert-cache":
                return INTEROP_CONVERT_CACHE;
            case "ruby.time-format-cache":
                return TIME_FORMAT_CACHE;
            case "ruby.integer-pow-cache":
                return POW_CACHE;
            case "ruby.ruby-library-cache":
                return RUBY_LIBRARY_CACHE;
            case "ruby.thread-cache":
                return THREAD_CACHE;
            case "ruby.context-identity-cache":
                return CONTEXT_SPECIFIC_IDENTITY_CACHE;
            case "ruby.identity-cache":
                return IDENTITY_CACHE;
            case "ruby.class-cache":
                return CLASS_CACHE;
            case "ruby.array-dup-cache":
                return ARRAY_DUP_CACHE;
            case "ruby.array-strategy-cache":
                return ARRAY_STRATEGY_CACHE;
            case "ruby.array-uninitialized-size":
                return ARRAY_UNINITIALIZED_SIZE;
            case "ruby.hash-packed-array-max":
                return HASH_PACKED_ARRAY_MAX;
            case "ruby.pack-unroll":
                return PACK_UNROLL_LIMIT;
            case "ruby.pack-recover":
                return PACK_RECOVER_LOOP_MIN;
            case "ruby.regexp-instrument-creation":
                return REGEXP_INSTRUMENT_CREATION;
            case "ruby.shared-objects":
                return SHARED_OBJECTS_ENABLED;
            case "ruby.shared-objects-debug":
                return SHARED_OBJECTS_DEBUG;
            case "ruby.shared-objects-force":
                return SHARED_OBJECTS_FORCE;
            default:
                return null;
        }
    }

    public static boolean areOptionsCompatible(OptionValues one, OptionValues two) {
        return one.get(OptionsCatalog.CORE_LOAD_PATH_KEY).equals(two.get(OptionsCatalog.CORE_LOAD_PATH_KEY)) &&
               one.get(OptionsCatalog.FROZEN_STRING_LITERALS_KEY).equals(two.get(OptionsCatalog.FROZEN_STRING_LITERALS_KEY)) &&
               one.get(OptionsCatalog.DEFAULT_LAZY_KEY).equals(two.get(OptionsCatalog.DEFAULT_LAZY_KEY)) &&
               one.get(OptionsCatalog.LAZY_CALLTARGETS_KEY).equals(two.get(OptionsCatalog.LAZY_CALLTARGETS_KEY)) &&
               one.get(OptionsCatalog.CORE_AS_INTERNAL_KEY).equals(two.get(OptionsCatalog.CORE_AS_INTERNAL_KEY)) &&
               one.get(OptionsCatalog.STDLIB_AS_INTERNAL_KEY).equals(two.get(OptionsCatalog.STDLIB_AS_INTERNAL_KEY)) &&
               one.get(OptionsCatalog.LAZY_TRANSLATION_USER_KEY).equals(two.get(OptionsCatalog.LAZY_TRANSLATION_USER_KEY)) &&
               one.get(OptionsCatalog.BACKTRACES_OMIT_UNUSED_KEY).equals(two.get(OptionsCatalog.BACKTRACES_OMIT_UNUSED_KEY)) &&
               one.get(OptionsCatalog.LAZY_TRANSLATION_LOG_KEY).equals(two.get(OptionsCatalog.LAZY_TRANSLATION_LOG_KEY)) &&
               one.get(OptionsCatalog.LOG_DYNAMIC_CONSTANT_LOOKUP_KEY).equals(two.get(OptionsCatalog.LOG_DYNAMIC_CONSTANT_LOOKUP_KEY)) &&
               one.get(OptionsCatalog.LAZY_BUILTINS_KEY).equals(two.get(OptionsCatalog.LAZY_BUILTINS_KEY)) &&
               one.get(OptionsCatalog.LAZY_TRANSLATION_CORE_KEY).equals(two.get(OptionsCatalog.LAZY_TRANSLATION_CORE_KEY)) &&
               one.get(OptionsCatalog.BASICOPS_INLINE_KEY).equals(two.get(OptionsCatalog.BASICOPS_INLINE_KEY)) &&
               one.get(OptionsCatalog.PROFILE_ARGUMENTS_KEY).equals(two.get(OptionsCatalog.PROFILE_ARGUMENTS_KEY)) &&
               one.get(OptionsCatalog.DEFAULT_CACHE_KEY).equals(two.get(OptionsCatalog.DEFAULT_CACHE_KEY)) &&
               one.get(OptionsCatalog.METHOD_LOOKUP_CACHE_KEY).equals(two.get(OptionsCatalog.METHOD_LOOKUP_CACHE_KEY)) &&
               one.get(OptionsCatalog.DISPATCH_CACHE_KEY).equals(two.get(OptionsCatalog.DISPATCH_CACHE_KEY)) &&
               one.get(OptionsCatalog.YIELD_CACHE_KEY).equals(two.get(OptionsCatalog.YIELD_CACHE_KEY)) &&
               one.get(OptionsCatalog.METHOD_TO_PROC_CACHE_KEY).equals(two.get(OptionsCatalog.METHOD_TO_PROC_CACHE_KEY)) &&
               one.get(OptionsCatalog.IS_A_CACHE_KEY).equals(two.get(OptionsCatalog.IS_A_CACHE_KEY)) &&
               one.get(OptionsCatalog.BIND_CACHE_KEY).equals(two.get(OptionsCatalog.BIND_CACHE_KEY)) &&
               one.get(OptionsCatalog.CONSTANT_CACHE_KEY).equals(two.get(OptionsCatalog.CONSTANT_CACHE_KEY)) &&
               one.get(OptionsCatalog.INSTANCE_VARIABLE_CACHE_KEY).equals(two.get(OptionsCatalog.INSTANCE_VARIABLE_CACHE_KEY)) &&
               one.get(OptionsCatalog.BINDING_LOCAL_VARIABLE_CACHE_KEY).equals(two.get(OptionsCatalog.BINDING_LOCAL_VARIABLE_CACHE_KEY)) &&
               one.get(OptionsCatalog.SYMBOL_TO_PROC_CACHE_KEY).equals(two.get(OptionsCatalog.SYMBOL_TO_PROC_CACHE_KEY)) &&
               one.get(OptionsCatalog.PACK_CACHE_KEY).equals(two.get(OptionsCatalog.PACK_CACHE_KEY)) &&
               one.get(OptionsCatalog.UNPACK_CACHE_KEY).equals(two.get(OptionsCatalog.UNPACK_CACHE_KEY)) &&
               one.get(OptionsCatalog.EVAL_CACHE_KEY).equals(two.get(OptionsCatalog.EVAL_CACHE_KEY)) &&
               one.get(OptionsCatalog.ENCODING_COMPATIBLE_QUERY_CACHE_KEY).equals(two.get(OptionsCatalog.ENCODING_COMPATIBLE_QUERY_CACHE_KEY)) &&
               one.get(OptionsCatalog.ENCODING_LOADED_CLASSES_CACHE_KEY).equals(two.get(OptionsCatalog.ENCODING_LOADED_CLASSES_CACHE_KEY)) &&
               one.get(OptionsCatalog.INTEROP_CONVERT_CACHE_KEY).equals(two.get(OptionsCatalog.INTEROP_CONVERT_CACHE_KEY)) &&
               one.get(OptionsCatalog.TIME_FORMAT_CACHE_KEY).equals(two.get(OptionsCatalog.TIME_FORMAT_CACHE_KEY)) &&
               one.get(OptionsCatalog.POW_CACHE_KEY).equals(two.get(OptionsCatalog.POW_CACHE_KEY)) &&
               one.get(OptionsCatalog.RUBY_LIBRARY_CACHE_KEY).equals(two.get(OptionsCatalog.RUBY_LIBRARY_CACHE_KEY)) &&
               one.get(OptionsCatalog.THREAD_CACHE_KEY).equals(two.get(OptionsCatalog.THREAD_CACHE_KEY)) &&
               one.get(OptionsCatalog.CONTEXT_SPECIFIC_IDENTITY_CACHE_KEY).equals(two.get(OptionsCatalog.CONTEXT_SPECIFIC_IDENTITY_CACHE_KEY)) &&
               one.get(OptionsCatalog.IDENTITY_CACHE_KEY).equals(two.get(OptionsCatalog.IDENTITY_CACHE_KEY)) &&
               one.get(OptionsCatalog.CLASS_CACHE_KEY).equals(two.get(OptionsCatalog.CLASS_CACHE_KEY)) &&
               one.get(OptionsCatalog.ARRAY_DUP_CACHE_KEY).equals(two.get(OptionsCatalog.ARRAY_DUP_CACHE_KEY)) &&
               one.get(OptionsCatalog.ARRAY_STRATEGY_CACHE_KEY).equals(two.get(OptionsCatalog.ARRAY_STRATEGY_CACHE_KEY)) &&
               one.get(OptionsCatalog.ARRAY_UNINITIALIZED_SIZE_KEY).equals(two.get(OptionsCatalog.ARRAY_UNINITIALIZED_SIZE_KEY)) &&
               one.get(OptionsCatalog.HASH_PACKED_ARRAY_MAX_KEY).equals(two.get(OptionsCatalog.HASH_PACKED_ARRAY_MAX_KEY)) &&
               one.get(OptionsCatalog.PACK_UNROLL_LIMIT_KEY).equals(two.get(OptionsCatalog.PACK_UNROLL_LIMIT_KEY)) &&
               one.get(OptionsCatalog.PACK_RECOVER_LOOP_MIN_KEY).equals(two.get(OptionsCatalog.PACK_RECOVER_LOOP_MIN_KEY)) &&
               one.get(OptionsCatalog.REGEXP_INSTRUMENT_CREATION_KEY).equals(two.get(OptionsCatalog.REGEXP_INSTRUMENT_CREATION_KEY)) &&
               one.get(OptionsCatalog.SHARED_OBJECTS_ENABLED_KEY).equals(two.get(OptionsCatalog.SHARED_OBJECTS_ENABLED_KEY)) &&
               one.get(OptionsCatalog.SHARED_OBJECTS_DEBUG_KEY).equals(two.get(OptionsCatalog.SHARED_OBJECTS_DEBUG_KEY)) &&
               one.get(OptionsCatalog.SHARED_OBJECTS_FORCE_KEY).equals(two.get(OptionsCatalog.SHARED_OBJECTS_FORCE_KEY));
    }

    public static boolean areOptionsCompatibleOrLog(TruffleLogger logger, LanguageOptions oldOptions, LanguageOptions newOptions) {
        Object oldValue;
        Object newValue;

        oldValue = oldOptions.CORE_LOAD_PATH;
        newValue = newOptions.CORE_LOAD_PATH;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --core-load-path differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.FROZEN_STRING_LITERALS;
        newValue = newOptions.FROZEN_STRING_LITERALS;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --frozen-string-literals differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.DEFAULT_LAZY;
        newValue = newOptions.DEFAULT_LAZY;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --lazy-default differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.LAZY_CALLTARGETS;
        newValue = newOptions.LAZY_CALLTARGETS;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --lazy-calltargets differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.CORE_AS_INTERNAL;
        newValue = newOptions.CORE_AS_INTERNAL;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --core-as-internal differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.STDLIB_AS_INTERNAL;
        newValue = newOptions.STDLIB_AS_INTERNAL;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --stdlib-as-internal differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.LAZY_TRANSLATION_USER;
        newValue = newOptions.LAZY_TRANSLATION_USER;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --lazy-translation-user differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.BACKTRACES_OMIT_UNUSED;
        newValue = newOptions.BACKTRACES_OMIT_UNUSED;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --backtraces-omit-unused differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.LAZY_TRANSLATION_LOG;
        newValue = newOptions.LAZY_TRANSLATION_LOG;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --lazy-translation-log differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.LOG_DYNAMIC_CONSTANT_LOOKUP;
        newValue = newOptions.LOG_DYNAMIC_CONSTANT_LOOKUP;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --constant-dynamic-lookup-log differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.LAZY_BUILTINS;
        newValue = newOptions.LAZY_BUILTINS;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --lazy-builtins differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.LAZY_TRANSLATION_CORE;
        newValue = newOptions.LAZY_TRANSLATION_CORE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --lazy-translation-core differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.BASICOPS_INLINE;
        newValue = newOptions.BASICOPS_INLINE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --basic-ops-inline differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.PROFILE_ARGUMENTS;
        newValue = newOptions.PROFILE_ARGUMENTS;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --profile-arguments differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.DEFAULT_CACHE;
        newValue = newOptions.DEFAULT_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --default-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.METHOD_LOOKUP_CACHE;
        newValue = newOptions.METHOD_LOOKUP_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --method-lookup-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.DISPATCH_CACHE;
        newValue = newOptions.DISPATCH_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --dispatch-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.YIELD_CACHE;
        newValue = newOptions.YIELD_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --yield-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.METHOD_TO_PROC_CACHE;
        newValue = newOptions.METHOD_TO_PROC_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --to-proc-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.IS_A_CACHE;
        newValue = newOptions.IS_A_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --is-a-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.BIND_CACHE;
        newValue = newOptions.BIND_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --bind-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.CONSTANT_CACHE;
        newValue = newOptions.CONSTANT_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --constant-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.INSTANCE_VARIABLE_CACHE;
        newValue = newOptions.INSTANCE_VARIABLE_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --instance-variable-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.BINDING_LOCAL_VARIABLE_CACHE;
        newValue = newOptions.BINDING_LOCAL_VARIABLE_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --binding-local-variable-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.SYMBOL_TO_PROC_CACHE;
        newValue = newOptions.SYMBOL_TO_PROC_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --symbol-to-proc-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.PACK_CACHE;
        newValue = newOptions.PACK_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --pack-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.UNPACK_CACHE;
        newValue = newOptions.UNPACK_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --unpack-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.EVAL_CACHE;
        newValue = newOptions.EVAL_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --eval-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.ENCODING_COMPATIBLE_QUERY_CACHE;
        newValue = newOptions.ENCODING_COMPATIBLE_QUERY_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --encoding-compatible-query-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.ENCODING_LOADED_CLASSES_CACHE;
        newValue = newOptions.ENCODING_LOADED_CLASSES_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --encoding-loaded-classes-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.INTEROP_CONVERT_CACHE;
        newValue = newOptions.INTEROP_CONVERT_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --interop-convert-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.TIME_FORMAT_CACHE;
        newValue = newOptions.TIME_FORMAT_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --time-format-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.POW_CACHE;
        newValue = newOptions.POW_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --integer-pow-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.RUBY_LIBRARY_CACHE;
        newValue = newOptions.RUBY_LIBRARY_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --ruby-library-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.THREAD_CACHE;
        newValue = newOptions.THREAD_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --thread-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.CONTEXT_SPECIFIC_IDENTITY_CACHE;
        newValue = newOptions.CONTEXT_SPECIFIC_IDENTITY_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --context-identity-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.IDENTITY_CACHE;
        newValue = newOptions.IDENTITY_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --identity-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.CLASS_CACHE;
        newValue = newOptions.CLASS_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --class-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.ARRAY_DUP_CACHE;
        newValue = newOptions.ARRAY_DUP_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --array-dup-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.ARRAY_STRATEGY_CACHE;
        newValue = newOptions.ARRAY_STRATEGY_CACHE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --array-strategy-cache differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.ARRAY_UNINITIALIZED_SIZE;
        newValue = newOptions.ARRAY_UNINITIALIZED_SIZE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --array-uninitialized-size differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.HASH_PACKED_ARRAY_MAX;
        newValue = newOptions.HASH_PACKED_ARRAY_MAX;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --hash-packed-array-max differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.PACK_UNROLL_LIMIT;
        newValue = newOptions.PACK_UNROLL_LIMIT;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --pack-unroll differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.PACK_RECOVER_LOOP_MIN;
        newValue = newOptions.PACK_RECOVER_LOOP_MIN;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --pack-recover differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.REGEXP_INSTRUMENT_CREATION;
        newValue = newOptions.REGEXP_INSTRUMENT_CREATION;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --regexp-instrument-creation differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.SHARED_OBJECTS_ENABLED;
        newValue = newOptions.SHARED_OBJECTS_ENABLED;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --shared-objects differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.SHARED_OBJECTS_DEBUG;
        newValue = newOptions.SHARED_OBJECTS_DEBUG;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --shared-objects-debug differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        oldValue = oldOptions.SHARED_OBJECTS_FORCE;
        newValue = newOptions.SHARED_OBJECTS_FORCE;
        if (!newValue.equals(oldValue)) {
            logger.fine("not reusing pre-initialized context: --shared-objects-force differs, was: " + oldValue + " and is now: " + newValue);
            return false;
        }

        return true;
    }
}
// @formatter:on
