#!/usr/bin/env ruby

# Copyright (c) 2020, 2024 Oracle and/or its affiliates. All rights reserved.
# This code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
#
# Eclipse Public License version 2.0, or
# GNU General Public License version 2, or
# GNU Lesser General Public License version 2.1.

copyright = File.read(__FILE__)[/Copyright \(c\) \d+, \d+ Oracle/]

file = 'tool/id.def'
ids = eval(File.read(file), binding, file)
types = ids.keys.grep(/^[A-Z]/)

character_ids = ('!'..'~').select { |c| c !~ /^[[:alnum:]]$/ && c != '_' }.to_a

header = <<JAVA
/*
 * #{copyright} and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 */
package org.truffleruby.core.symbol;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.strings.TruffleString;
import org.truffleruby.core.encoding.Encodings;
import org.truffleruby.core.encoding.TStringUtils;
import org.truffleruby.core.string.StringOperations;
import org.truffleruby.core.string.TStringConstants;

// GENERATED BY #{__FILE__}
// This file is automatically generated from tool/id.def with 'jt build core-symbols'

// @formatter:off
public final class CoreSymbols {

    public static final long STATIC_SYMBOL_ID = 0x1;
    private static final long GLOBAL_SYMBOL_ID = (0x03 << 1);

    public static final int STATIC_SYMBOLS_SIZE = <STATIC_SYMBOLS_SIZE>;

    public final List<RubySymbol> CORE_SYMBOLS = new ArrayList<>();
    public final RubySymbol[] STATIC_SYMBOLS = new RubySymbol[STATIC_SYMBOLS_SIZE];

    public final RubySymbol CLASS = createRubySymbol("class");
    public final RubySymbol NEW = createRubySymbol("new");
    public final RubySymbol IMMEDIATE = createRubySymbol("immediate");
    public final RubySymbol LINE = createRubySymbol("line");
    public final RubySymbol NEVER = createRubySymbol("never");
    public final RubySymbol ON_BLOCKING = createRubySymbol("on_blocking");
    public final RubySymbol DEPRECATED = createRubySymbol("deprecated");
    public final RubySymbol EXPERIMENTAL = createRubySymbol("experimental");
    public final RubySymbol PERFORMANCE = createRubySymbol("performance");
    public final RubySymbol BIG = createRubySymbol("big");
    public final RubySymbol LITTLE = createRubySymbol("little");
    public final RubySymbol NATIVE = createRubySymbol("native");

    // Added to workaround liquid's no symbols leaked test (SecurityTest#test_does_not_permanently_add_filters_to_symbol_table)
    public final RubySymbol IMMEDIATE_SWEEP = createRubySymbol("immediate_sweep");
    public final RubySymbol IMMEDIATE_MARK = createRubySymbol("immediate_mark");
    public final RubySymbol FULL_MARK = createRubySymbol("full_mark");

JAVA
lines = []

names = {
  "!" => "BANG",
  "\"" => "DOUBLE_QUOTE",
  "#" => "POUND",
  "$" => "DOLLAR",
  "%" => "MODULO",
  "&" => "AMPERSAND",
  "'" => "SINGLE_QUOTE",
  "(" => "LPAREN",
  ")" => "RPAREN",
  "*" => "MULTIPLY",
  "+" => "PLUS",
  "," => "COMMA",
  "-" => "MINUS",
  "." => "PERIOD",
  "/" => "DIVIDE",
  ":" => "COLON",
  ";" => "SEMICOLON",
  "<" => "LESS_THAN",
  "=" => "EQUAL",
  ">" => "GREATER_THAN",
  "?" => "QUESTION_MARK",
  "@" => "AT_SYMBOL",
  "[" => "LEFT_BRACKET",
  "\\" => "BACK_SLASH",
  "]" => "RIGHT_BRACKET",
  "^" => "CIRCUMFLEX",
  "`" => "BACK_TICK",
  "{" => "LEFT_BRACE",
  "|" => "PIPE",
  "}" => "RIGHT_BRACE",
  "~" => "TILDE",
}

ids_map = {}
character_ids.each do |id|
  ids_map[id.ord] = { :id => id, :name => names[id] }
end

min_index = ids_map.keys.min
lines << "    public static final int FIRST_OP_ID = #{min_index};"
lines << ''

ids_map.each do |key, value|
  lines << "    public final RubySymbol #{ value[:name] } = createRubySymbol(#{value[:id].inspect}, #{key});"
end

lines << ''

operators = {}
index = 128
ids[:token_op].each do |_name, op, token|
  if token.nil?
    # no ID
  elsif operators.include?(op)
    lines << "    // Skipped duplicated operator: #{token} #{op} #{index}"
    index += 1
  else
    operators[op] = true
    lines << "    public final RubySymbol #{token} = createRubySymbol(\"#{op}\", #{index});"
    index += 1
  end
end

lines << ''

ids[:preserved].each do |token|
  if ids[:predefined][token]
    name = token.start_with?('_') ? token[1..-1].upcase : token.upcase
    symbol = token == 'NULL' ?  '' : ids[:predefined][token]
    index
    lines << "    public final RubySymbol #{name} = createRubySymbol(#{symbol.inspect}, #{index});"
    index += 1
  else
    lines << "    // Skipped preserved token: `#{token}`"
    index += 1
  end
end

lines << ''
lines << "    public static final int LAST_OP_ID = #{index-1};"
lines << ''

types.each do |type|
  tokens = ids[type]
  tokens.each do |token|
    lines << "    public final RubySymbol #{token.upcase} = createRubySymbol(\"#{ids[:predefined][token]}\", to#{type.capitalize}(#{index}));"
    index += 1
  end
end

lines << ''
lines << ''

static_symbols_size = index

footer = <<JAVA
    public RubySymbol createRubySymbol(String string, long id) {
        TruffleString tstring = TStringConstants.lookupUSASCIITString(string);
        if (tstring == null) {
            byte[] bytes = StringOperations.encodeAsciiBytes(string);
            tstring = TStringUtils.fromByteArray(bytes, TruffleString.Encoding.US_ASCII);
        }

        final RubySymbol symbol = new RubySymbol(string, tstring, Encodings.US_ASCII, id);
        CORE_SYMBOLS.add(symbol);

        if (id != RubySymbol.UNASSIGNED_ID) {
            final int index = idToIndex(id);
            STATIC_SYMBOLS[index] = symbol;
        }
        return symbol;
    }

    public RubySymbol createRubySymbol(String string) {
        return createRubySymbol(string, RubySymbol.UNASSIGNED_ID);
    }

    public static int idToIndex(long id) {
      final int index;
      if (id > LAST_OP_ID) {
        index = (int) id >> 4;
      } else {
        index = (int) id;
      }
      assert index < STATIC_SYMBOLS_SIZE;
      return index;
    }

    private static long toLocal(long id) {
        return id << 4 | STATIC_SYMBOL_ID;
    }

    private static long toGlobal(long id) {
        return id << 4 | STATIC_SYMBOL_ID | GLOBAL_SYMBOL_ID;
    }

    public static boolean isStaticSymbol(long value) {
        return (value >= FIRST_OP_ID && value <= LAST_OP_ID) ||
                ((value & STATIC_SYMBOL_ID) == STATIC_SYMBOL_ID && (value >> 4) < STATIC_SYMBOLS_SIZE);
    }

}
// @formatter:on
JAVA

contents = header.sub('<STATIC_SYMBOLS_SIZE>', static_symbols_size.to_s) + lines.join("\n") + footer
File.write('src/main/java/org/truffleruby/core/symbol/CoreSymbols.java', contents)
