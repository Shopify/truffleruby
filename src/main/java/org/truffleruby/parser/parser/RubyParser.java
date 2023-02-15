/*
 ***** BEGIN LICENSE BLOCK *****
 * Version: EPL 2.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/epl-v20.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2008-2017 Thomas E Enebo <enebo@acm.org>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the EPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the EPL, the GPL or the LGPL.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 2.0, or
 * GNU General Public License version 2, or
 * GNU Lesser General Public License version 2.1.
 ***** END LICENSE BLOCK *****/
// created by jay 1.0.2 (c) 2002-2004 ats@cs.rit.edu
// skeleton Java 1.0 (c) 2002 ats@cs.rit.edu

// line 2 "RubyParser.y"
package org.truffleruby.parser.parser;

import com.oracle.truffle.api.strings.TruffleString;

import org.truffleruby.Layouts;
import org.truffleruby.annotations.SuppressFBWarnings;
import org.truffleruby.core.encoding.Encodings;
import org.truffleruby.core.encoding.RubyEncoding;
import org.truffleruby.core.encoding.TStringUtils;
import org.truffleruby.core.string.TStringConstants;
import org.truffleruby.language.SourceIndexLength;
import org.truffleruby.parser.MethodStubHolder;
import org.truffleruby.parser.RubyDeferredWarnings;
import org.truffleruby.parser.ast.ArgsParseNode;
import org.truffleruby.parser.ast.ArgumentParseNode;
import org.truffleruby.parser.ast.ArrayParseNode;
import org.truffleruby.parser.ast.AssignableParseNode;
import org.truffleruby.parser.ast.BackRefParseNode;
import org.truffleruby.parser.ast.BeginParseNode;
import org.truffleruby.parser.ast.BlockAcceptingParseNode;
import org.truffleruby.parser.ast.BlockArgParseNode;
import org.truffleruby.parser.ast.BlockParseNode;
import org.truffleruby.parser.ast.BlockPassParseNode;
import org.truffleruby.parser.ast.BreakParseNode;
import org.truffleruby.parser.ast.ClassParseNode;
import org.truffleruby.parser.ast.ClassVarAsgnParseNode;
import org.truffleruby.parser.ast.ClassVarParseNode;
import org.truffleruby.parser.ast.Colon3ParseNode;
import org.truffleruby.parser.ast.ConstDeclParseNode;
import org.truffleruby.parser.ast.ConstParseNode;
import org.truffleruby.parser.ast.DStrParseNode;
import org.truffleruby.parser.ast.DSymbolParseNode;
import org.truffleruby.parser.ast.DXStrParseNode;
import org.truffleruby.parser.ast.DefnParseNode;
import org.truffleruby.parser.ast.DefsParseNode;
import org.truffleruby.parser.ast.DotParseNode;
import org.truffleruby.parser.ast.EncodingParseNode;
import org.truffleruby.parser.ast.EnsureParseNode;
import org.truffleruby.parser.ast.EvStrParseNode;
import org.truffleruby.parser.ast.FCallParseNode;
import org.truffleruby.parser.ast.FalseParseNode;
import org.truffleruby.parser.ast.FileParseNode;
import org.truffleruby.parser.ast.FixnumParseNode;
import org.truffleruby.parser.ast.FloatParseNode;
import org.truffleruby.parser.ast.ForParseNode;
import org.truffleruby.parser.ast.GlobalAsgnParseNode;
import org.truffleruby.parser.ast.GlobalVarParseNode;
import org.truffleruby.parser.ast.HashParseNode;
import org.truffleruby.parser.ast.IfParseNode;
import org.truffleruby.parser.ast.InstAsgnParseNode;
import org.truffleruby.parser.ast.InstVarParseNode;
import org.truffleruby.parser.ast.IterParseNode;
import org.truffleruby.parser.ast.LambdaParseNode;
import org.truffleruby.parser.ast.ListParseNode;
import org.truffleruby.parser.ast.LiteralParseNode;
import org.truffleruby.parser.ast.LocalVarParseNode;
import org.truffleruby.parser.ast.ModuleParseNode;
import org.truffleruby.parser.ast.MultipleAsgnParseNode;
import org.truffleruby.parser.ast.NextParseNode;
import org.truffleruby.parser.ast.NilImplicitParseNode;
import org.truffleruby.parser.ast.NilParseNode;
import org.truffleruby.parser.ast.NonLocalControlFlowParseNode;
import org.truffleruby.parser.ast.NumericParseNode;
import org.truffleruby.parser.ast.OpAsgnAndParseNode;
import org.truffleruby.parser.ast.OpAsgnOrParseNode;
import org.truffleruby.parser.ast.OptArgParseNode;
import org.truffleruby.parser.ast.ParseNode;
import org.truffleruby.parser.ast.PostExeParseNode;
import org.truffleruby.parser.ast.PreExe19ParseNode;
import org.truffleruby.parser.ast.RationalParseNode;
import org.truffleruby.parser.ast.RedoParseNode;
import org.truffleruby.parser.ast.RegexpParseNode;
import org.truffleruby.parser.ast.RequiredKeywordArgumentValueParseNode;
import org.truffleruby.parser.ast.RescueBodyParseNode;
import org.truffleruby.parser.ast.RescueParseNode;
import org.truffleruby.parser.ast.RestArgParseNode;
import org.truffleruby.parser.ast.RetryParseNode;
import org.truffleruby.parser.ast.ReturnParseNode;
import org.truffleruby.parser.ast.SClassParseNode;
import org.truffleruby.parser.ast.SelfParseNode;
import org.truffleruby.parser.ast.SplatParseNode;
import org.truffleruby.parser.ast.StarParseNode;
import org.truffleruby.parser.ast.StrParseNode;
import org.truffleruby.parser.ast.TrueParseNode;
import org.truffleruby.parser.ast.UnnamedRestArgParseNode;
import org.truffleruby.parser.ast.UntilParseNode;
import org.truffleruby.parser.ast.VAliasParseNode;
import org.truffleruby.parser.ast.WhileParseNode;
import org.truffleruby.parser.ast.XStrParseNode;
import org.truffleruby.parser.ast.YieldParseNode;
import org.truffleruby.parser.ast.ZArrayParseNode;
import org.truffleruby.parser.ast.ZSuperParseNode;
import org.truffleruby.parser.ast.types.ILiteralNode;
import org.truffleruby.parser.lexer.LexerSource;
import org.truffleruby.parser.lexer.RubyLexer;
import org.truffleruby.parser.lexer.StrTerm;
import org.truffleruby.parser.lexer.SyntaxException.PID;

import static org.truffleruby.parser.lexer.RubyLexer.EXPR_BEG;
import static org.truffleruby.parser.lexer.RubyLexer.EXPR_END;
import static org.truffleruby.parser.lexer.RubyLexer.EXPR_ENDARG;
import static org.truffleruby.parser.lexer.RubyLexer.EXPR_ENDFN;
import static org.truffleruby.parser.lexer.RubyLexer.EXPR_FITEM;
import static org.truffleruby.parser.lexer.RubyLexer.EXPR_FNAME;
import static org.truffleruby.parser.lexer.RubyLexer.EXPR_LABEL;
import static org.truffleruby.parser.parser.ParserSupport.value_expr;

// @formatter:off
// CheckStyle: start generated
@SuppressFBWarnings("IP")
@SuppressWarnings({"unchecked", "fallthrough", "cast"})
public class RubyParser {
    protected final ParserSupport support;
    protected final RubyLexer lexer;

    public RubyParser(LexerSource source, RubyDeferredWarnings warnings) {
        this.support = new ParserSupport(source, warnings);
        this.lexer = new RubyLexer(support, source, warnings);
        support.setLexer(lexer);
    }
// line 126 "-"
  // %token constants
  public static final int keyword_class = 257;
  public static final int keyword_module = 258;
  public static final int keyword_def = 259;
  public static final int keyword_undef = 260;
  public static final int keyword_begin = 261;
  public static final int keyword_rescue = 262;
  public static final int keyword_ensure = 263;
  public static final int keyword_end = 264;
  public static final int keyword_if = 265;
  public static final int keyword_unless = 266;
  public static final int keyword_then = 267;
  public static final int keyword_elsif = 268;
  public static final int keyword_else = 269;
  public static final int keyword_case = 270;
  public static final int keyword_when = 271;
  public static final int keyword_while = 272;
  public static final int keyword_until = 273;
  public static final int keyword_for = 274;
  public static final int keyword_break = 275;
  public static final int keyword_next = 276;
  public static final int keyword_redo = 277;
  public static final int keyword_retry = 278;
  public static final int keyword_in = 279;
  public static final int keyword_do = 280;
  public static final int keyword_do_cond = 281;
  public static final int keyword_do_block = 282;
  public static final int keyword_return = 283;
  public static final int keyword_yield = 284;
  public static final int keyword_super = 285;
  public static final int keyword_self = 286;
  public static final int keyword_nil = 287;
  public static final int keyword_true = 288;
  public static final int keyword_false = 289;
  public static final int keyword_and = 290;
  public static final int keyword_or = 291;
  public static final int keyword_not = 292;
  public static final int modifier_if = 293;
  public static final int modifier_unless = 294;
  public static final int modifier_while = 295;
  public static final int modifier_until = 296;
  public static final int modifier_rescue = 297;
  public static final int keyword_alias = 298;
  public static final int keyword_defined = 299;
  public static final int keyword_BEGIN = 300;
  public static final int keyword_END = 301;
  public static final int keyword__LINE__ = 302;
  public static final int keyword__FILE__ = 303;
  public static final int keyword__ENCODING__ = 304;
  public static final int keyword_do_lambda = 305;
  public static final int tIDENTIFIER = 306;
  public static final int tFID = 307;
  public static final int tGVAR = 308;
  public static final int tIVAR = 309;
  public static final int tCONSTANT = 310;
  public static final int tCVAR = 311;
  public static final int tLABEL = 312;
  public static final int tCHAR = 313;
  public static final int tUPLUS = 314;
  public static final int tUMINUS = 315;
  public static final int tUMINUS_NUM = 316;
  public static final int tPOW = 317;
  public static final int tCMP = 318;
  public static final int tEQ = 319;
  public static final int tEQQ = 320;
  public static final int tNEQ = 321;
  public static final int tGEQ = 322;
  public static final int tLEQ = 323;
  public static final int tANDOP = 324;
  public static final int tOROP = 325;
  public static final int tMATCH = 326;
  public static final int tNMATCH = 327;
  public static final int tDOT = 328;
  public static final int tDOT2 = 329;
  public static final int tDOT3 = 330;
  public static final int tBDOT2 = 331;
  public static final int tBDOT3 = 332;
  public static final int tAREF = 333;
  public static final int tASET = 334;
  public static final int tLSHFT = 335;
  public static final int tRSHFT = 336;
  public static final int tANDDOT = 337;
  public static final int tCOLON2 = 338;
  public static final int tCOLON3 = 339;
  public static final int tOP_ASGN = 340;
  public static final int tASSOC = 341;
  public static final int tLPAREN = 342;
  public static final int tLPAREN2 = 343;
  public static final int tRPAREN = 344;
  public static final int tLPAREN_ARG = 345;
  public static final int tLBRACK = 346;
  public static final int tRBRACK = 347;
  public static final int tLBRACE = 348;
  public static final int tLBRACE_ARG = 349;
  public static final int tSTAR = 350;
  public static final int tSTAR2 = 351;
  public static final int tAMPER = 352;
  public static final int tAMPER2 = 353;
  public static final int tTILDE = 354;
  public static final int tPERCENT = 355;
  public static final int tDIVIDE = 356;
  public static final int tPLUS = 357;
  public static final int tMINUS = 358;
  public static final int tLT = 359;
  public static final int tGT = 360;
  public static final int tPIPE = 361;
  public static final int tBANG = 362;
  public static final int tCARET = 363;
  public static final int tLCURLY = 364;
  public static final int tRCURLY = 365;
  public static final int tBACK_REF2 = 366;
  public static final int tSYMBEG = 367;
  public static final int tSTRING_BEG = 368;
  public static final int tXSTRING_BEG = 369;
  public static final int tREGEXP_BEG = 370;
  public static final int tWORDS_BEG = 371;
  public static final int tQWORDS_BEG = 372;
  public static final int tSTRING_DBEG = 373;
  public static final int tSTRING_DVAR = 374;
  public static final int tSTRING_END = 375;
  public static final int tLAMBDA = 376;
  public static final int tLAMBEG = 377;
  public static final int tNTH_REF = 378;
  public static final int tBACK_REF = 379;
  public static final int tSTRING_CONTENT = 380;
  public static final int tINTEGER = 381;
  public static final int tIMAGINARY = 382;
  public static final int tFLOAT = 383;
  public static final int tRATIONAL = 384;
  public static final int tREGEXP_END = 385;
  public static final int tSYMBOLS_BEG = 386;
  public static final int tQSYMBOLS_BEG = 387;
  public static final int tDSTAR = 388;
  public static final int tSTRING_DEND = 389;
  public static final int tLABEL_END = 390;
  public static final int tLOWEST = 391;
  public static final int yyErrorCode = 256;

  /** number of final state.
    */
  protected static final int yyFinal = 1;

  /** parser tables.
      Order is mandated by <i>jay</i>.
    */
  protected static final short[] yyLhs = {
//yyLhs 691
    -1,   159,     0,   145,   146,   146,   146,   146,   147,   147,
    40,    39,    41,    41,    41,    41,    47,   162,    47,   163,
    42,    42,    42,    42,    42,    42,    42,    42,    42,    42,
    42,    42,    42,    42,    42,    42,    42,    31,    31,    31,
    31,    31,    31,    31,    31,    31,    65,    65,    65,    43,
    43,    43,    43,    43,    43,    35,    36,   167,    37,    48,
    32,    32,    64,    64,   119,   155,    46,    46,    46,    46,
    46,    46,    46,    46,    46,    46,    46,   122,   122,   133,
   133,   123,   123,   123,   123,   123,   123,   123,   123,   123,
   123,    79,    79,   109,   109,   110,   110,    80,    80,    80,
    80,    80,    80,    80,    80,    80,    80,    80,    80,    80,
    80,    80,    80,    80,    80,    80,    86,    86,    86,    86,
    86,    86,    86,    86,    86,    86,    86,    86,    86,    86,
    86,    86,    86,    86,    86,     8,     8,    30,    30,    30,
     7,     7,     7,     7,     7,   126,   126,   127,   127,    68,
   168,    68,     6,     6,     6,     6,     6,     6,     6,     6,
     6,     6,     6,     6,     6,     6,     6,     6,     6,     6,
     6,     6,     6,     6,     6,     6,     6,     6,     6,     6,
     6,     6,   140,   140,   140,   140,   140,   140,   140,   140,
   140,   140,   140,   140,   140,   140,   140,   140,   140,   140,
   140,   140,   140,   140,   140,   140,   140,   140,   140,   140,
   140,   140,   140,   140,   140,   140,   140,   140,   140,   140,
   140,   140,   140,   140,    44,    44,    44,    44,    44,    44,
    44,    44,    44,    44,    44,    44,    44,    44,    44,    44,
    44,    44,    44,    44,    44,    44,    44,    44,    44,    44,
    44,    44,    44,    44,    44,    44,    44,    44,    44,    44,
    44,    44,    44,    44,    44,    44,    44,    44,    44,    44,
    44,   142,   142,   142,   142,    55,    55,    81,    85,    85,
    85,    85,    66,    66,    58,    58,    58,    62,    62,   136,
   136,   136,   136,   136,    56,    56,    56,    56,    56,   170,
    60,   113,   113,   112,   112,    88,    88,    88,    88,    38,
    38,    78,    78,    78,    45,    45,    45,    45,    45,    45,
    45,    45,    45,    45,    45,   171,    45,   172,    45,   173,
   174,    45,    45,    45,    45,    45,    45,    45,    45,    45,
    45,    45,    45,    45,    45,    45,    45,    45,    45,    45,
   176,   178,    45,   179,   180,    45,    45,    45,    45,   181,
   182,    45,   183,    45,   185,    45,   186,    45,    45,    45,
   188,   189,    45,   190,   191,    45,    45,    45,    45,    45,
    49,   157,   158,   166,   187,   156,   175,   175,   175,   177,
   177,    53,    53,    50,    50,   135,   135,   137,   137,    93,
    93,   138,   138,   138,   138,   138,   138,   138,   138,   138,
   100,   100,   100,   100,   100,    99,    99,    74,    74,    74,
    74,    74,    74,    74,    74,    74,    74,    74,    74,    74,
    74,    74,    76,   193,    76,    75,    75,    75,   130,   130,
   129,   129,   139,   139,   194,   195,   132,   196,    73,   197,
    73,    73,   131,   131,   118,    63,    63,    63,    63,    22,
    22,    22,    22,    22,    22,    22,    22,    22,   117,   117,
   198,   199,   120,   200,   201,   121,    82,    51,    51,    83,
    52,    52,   124,   124,    84,    84,    84,    54,    54,    57,
    57,    28,    28,    28,    15,    16,    16,    16,    17,    18,
    19,    25,    90,    90,    27,    27,    96,    94,    94,    26,
    97,    89,    89,    95,    95,    20,    20,    21,    21,    24,
    24,    23,   202,    23,   203,   204,   205,   206,   207,    23,
    69,    69,    69,    69,     2,     1,     1,     1,     1,    29,
    33,    33,    34,    34,    34,    34,    61,    61,    61,    61,
    61,    61,    61,    61,    61,    61,    61,    61,   125,   125,
   125,   125,   125,   125,   125,   125,   125,   125,   125,   125,
    70,    70,   208,    59,    59,   164,   164,   209,    77,   210,
    77,   101,   101,   101,   101,   101,    98,    98,    71,    71,
    72,    72,    72,    72,    72,    72,    72,    72,    72,    72,
    72,    72,    72,    72,    72,    72,   151,   141,   141,   141,
   141,     9,     9,   154,   128,   128,    91,    91,   150,   102,
   102,   103,   103,   104,   104,   105,   105,   148,   148,   192,
   149,   149,    67,   134,   111,   111,    92,    92,    10,    10,
    13,    13,    12,    12,   116,   116,   115,   115,    14,   211,
    14,   106,   106,   107,   107,   108,   108,   108,   108,   108,
     3,     3,     3,     4,     4,     4,     4,     5,     5,     5,
    11,    11,   152,   152,   153,   153,   160,   160,   165,   165,
   143,   144,   169,   169,   169,   184,   184,   161,   161,    87,
   114,
    }, yyLen = {
//yyLen 691
     2,     0,     2,     2,     1,     1,     3,     2,     1,     4,
     4,     2,     1,     1,     3,     2,     1,     0,     5,     0,
     4,     3,     3,     3,     2,     3,     3,     3,     3,     3,
     4,     1,     3,     3,     5,     3,     1,     3,     3,     6,
     5,     5,     5,     5,     4,     3,     1,     3,     1,     1,
     3,     3,     3,     2,     1,     1,     2,     0,     5,     1,
     1,     1,     1,     4,     3,     1,     2,     3,     4,     5,
     4,     5,     2,     2,     2,     2,     2,     1,     3,     1,
     3,     1,     2,     3,     5,     2,     4,     2,     4,     1,
     3,     1,     3,     2,     3,     1,     3,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     4,
     3,     3,     3,     3,     2,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     4,     3,
     3,     3,     3,     2,     1,     1,     1,     2,     1,     3,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     0,     4,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     3,     3,     6,     5,     5,     5,
     5,     4,     3,     3,     2,     2,     3,     2,     2,     3,
     3,     3,     3,     3,     3,     4,     2,     2,     3,     3,
     3,     3,     1,     3,     3,     3,     3,     3,     2,     2,
     3,     3,     3,     3,     3,     6,     4,     6,     4,     6,
     1,     1,     1,     1,     1,     3,     3,     1,     1,     2,
     4,     2,     1,     3,     3,     5,     3,     1,     1,     1,
     1,     2,     4,     2,     1,     2,     2,     4,     1,     0,
     2,     2,     1,     2,     1,     1,     2,     3,     4,     1,
     1,     3,     4,     2,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     0,     4,     0,     3,     0,
     0,     5,     3,     3,     2,     3,     3,     1,     4,     3,
     1,     5,     4,     3,     2,     1,     2,     2,     6,     6,
     0,     0,     7,     0,     0,     7,     5,     4,     5,     0,
     0,     9,     0,     6,     0,     7,     0,     5,     4,     4,
     0,     0,     7,     0,     0,     9,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     2,     1,
     1,     1,     5,     1,     2,     1,     1,     1,     3,     1,
     3,     1,     4,     6,     3,     5,     2,     4,     1,     3,
     4,     2,     2,     2,     1,     2,     0,     6,     8,     4,
     6,     4,     2,     6,     2,     4,     6,     2,     4,     2,
     4,     1,     1,     0,     2,     3,     1,     4,     1,     4,
     1,     3,     1,     1,     0,     0,     4,     0,     5,     0,
     2,     0,     3,     3,     3,     2,     4,     5,     5,     2,
     4,     4,     3,     3,     3,     2,     1,     4,     3,     3,
     0,     0,     4,     0,     0,     4,     5,     1,     1,     5,
     1,     1,     6,     0,     1,     1,     1,     2,     1,     2,
     1,     1,     1,     1,     1,     1,     1,     2,     3,     3,
     3,     4,     0,     3,     1,     2,     4,     0,     3,     4,
     4,     0,     3,     0,     3,     0,     2,     0,     2,     0,
     2,     1,     0,     3,     0,     0,     0,     0,     0,     8,
     1,     1,     1,     1,     2,     1,     1,     1,     1,     3,
     1,     2,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     0,     4,     0,     1,     1,     3,     3,     0,
     3,     4,     2,     2,     2,     1,     2,     0,     1,     0,
     6,     8,     4,     6,     4,     6,     2,     4,     6,     2,
     4,     2,     4,     1,     3,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     3,     1,     3,     1,     2,
     1,     2,     1,     1,     3,     1,     3,     1,     1,     2,
     2,     1,     3,     3,     1,     3,     1,     3,     1,     1,
     2,     1,     1,     1,     2,     1,     2,     0,     1,     0,
     4,     1,     2,     1,     3,     3,     2,     1,     4,     2,
     1,     1,     1,     1,     1,     1,     1,     1,     1,     1,
     1,     1,     1,     1,     1,     1,     0,     1,     0,     1,
     2,     2,     0,     1,     1,     1,     1,     1,     2,     0,
     0,
    }, yyDefRed = {
//yyDefRed 1179
     1,     0,     0,     0,   381,   382,     0,     0,   325,     0,
     0,     0,   350,   353,     0,     0,     0,   378,   379,   385,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
   495,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,   515,   517,   519,     0,     0,
   444,   570,   571,   542,   545,   543,   544,     0,     0,   492,
    65,   315,     0,   496,   316,   317,     0,   318,   319,   314,
   493,    31,    49,   491,   540,     0,     0,     0,     0,     0,
     0,     0,     0,     0,   322,     0,    61,     0,     0,    91,
     0,     4,   320,   321,     0,     0,    77,     0,     2,     0,
     5,     0,     0,     0,     0,     0,     7,   192,   203,   193,
   216,   189,   209,   199,   198,   219,   220,   214,   197,   196,
   191,   217,   221,   222,   201,   190,   204,   208,   210,   202,
   195,   211,   218,   213,     0,     0,     0,     0,   188,   207,
   206,   223,   187,   194,   185,   186,     0,     0,     0,     0,
   142,   548,   547,     0,   550,   177,   178,   174,   155,   156,
   157,   164,   161,   163,   158,   159,   179,   180,   165,   166,
   649,   171,   170,   154,   176,   173,   172,   168,   169,   162,
   160,   152,   175,   153,   181,   167,   143,   370,     0,   648,
   144,   212,   205,   215,   200,   182,   183,   184,   140,   141,
   146,   145,   148,     0,   147,   149,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,   685,
   686,     0,     0,     0,   687,     0,     0,   376,   377,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,   380,
     0,     0,   395,   396,     0,     0,   337,     0,     0,     0,
     0,   515,     0,     0,   294,    75,     0,     0,     0,   653,
   298,    76,     0,    73,     0,     0,   465,    72,     0,   679,
     0,     0,    19,     0,     0,     0,   246,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,    13,
    12,     0,     0,     0,     0,     0,   278,     0,     0,     0,
   651,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
   259,    53,   258,   537,   536,   538,   534,   535,     0,     0,
     0,     0,   502,   511,   347,     0,   507,   513,   497,   473,
   470,   346,     0,     0,     0,   576,     0,   575,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,   273,   274,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,   272,
   271,     0,     0,     0,     0,   473,   455,   672,   673,     0,
     0,     0,     0,   675,   674,     0,     0,    93,     0,     0,
     0,     0,     0,     0,     3,     0,   459,     0,   344,    74,
   552,   551,   553,   554,   556,   555,   557,     0,     0,     0,
     0,   138,     0,     0,   323,   366,    55,     0,    56,     0,
   371,   670,   671,   373,   150,     0,     0,     0,     0,   387,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,   688,     0,     0,     0,   541,     0,     0,     0,
     0,   359,   656,   306,   301,     0,   659,     0,     0,   295,
   304,     0,   296,     0,   339,     0,   300,     0,   290,   289,
     0,     0,     0,     0,     0,   343,    52,    21,    23,    22,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,   332,    11,     0,     0,   328,     0,   335,     0,
   683,   279,     0,   281,   336,   652,     0,    95,     0,     0,
     0,     0,     0,   524,   522,   539,   521,   518,   498,   516,
   499,   500,   520,     0,     0,   447,   445,     0,     0,     0,
     0,   474,     0,   471,   612,   609,   608,   607,   610,   618,
   627,   606,     0,   639,   638,   643,   642,   628,   613,     0,
     0,     0,   636,     0,   588,     0,     0,   603,   625,     0,
   585,   616,   611,     0,     0,     0,   605,     0,     0,     0,
     0,     0,     0,     0,     0,    25,    26,    27,    28,    29,
    50,    51,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
   462,     0,   464,     0,     0,     0,   665,     0,     0,   666,
   463,     0,   663,   664,     0,    48,     0,     0,     0,    45,
   232,     0,     0,     0,     0,    37,   224,    33,   305,     0,
     0,     0,     0,    94,    32,     0,   309,     0,    38,   225,
     6,   470,    67,     0,   135,     0,   137,   572,   362,     0,
     0,     0,     0,    57,     0,     0,     0,     0,     0,     0,
   326,     0,     0,   388,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,   357,   390,   351,   389,   354,     0,
     0,     0,     0,     0,     0,     0,     0,     0,   655,     0,
     0,     0,   303,   654,   338,   680,     0,     0,   284,   286,
   342,    20,     0,     9,    30,     0,   231,     0,     0,    14,
     0,     0,     0,     0,     0,     0,     0,     0,     0,   525,
     0,   501,   504,     0,   509,     0,     0,     0,   450,   506,
     0,   510,     0,   469,     0,   468,     0,     0,     0,   397,
     0,   399,     0,   640,   644,     0,   601,   578,     0,   596,
     0,   599,     0,   582,   629,   630,     0,   583,   619,     0,
   584,   577,   384,   368,     0,    44,   580,   369,     0,     0,
   454,     0,     0,   461,     0,     0,     0,     0,     0,     0,
   288,     0,   460,   287,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,    92,     0,     0,     0,     0,     0,
     0,     0,     0,     0,   139,     0,     0,     0,   650,     0,
   374,   151,   485,     0,     0,   486,     0,     0,   393,     0,
   391,     0,     0,     0,     0,     0,     0,     0,     0,   356,
   358,     0,     0,     0,     0,     0,     0,   658,   308,   297,
     0,     0,   341,     0,   331,   280,    96,     0,   526,   530,
   531,   532,   523,   533,   503,   505,   512,     0,     0,     0,
   446,   508,   514,     0,   432,     0,     0,     0,     0,     0,
     0,   615,     0,   586,   614,     0,     0,   617,   604,     0,
   637,     0,   626,   646,     0,     0,   632,     0,     0,     0,
    43,   229,    42,   230,    71,     0,   681,    40,   227,    41,
   228,    69,   458,   457,    47,     0,     0,     0,     0,     0,
     0,     0,     0,     0,    34,    64,     0,     0,     0,   467,
   367,    58,     0,     0,     0,     0,   488,   394,     0,    10,
   490,     0,   348,     0,   349,     0,   307,     0,     0,     0,
   360,     0,   285,    18,   527,     0,     0,     0,     0,   475,
   436,     0,   434,   472,   398,     0,     0,     0,   400,     0,
   602,     0,   594,     0,   592,     0,   597,   600,   581,     0,
     0,     0,     0,    39,     0,     0,     0,   573,   363,   372,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,   487,     0,   489,     0,
     0,   477,   476,   478,   352,   355,     0,   528,   448,     0,
   453,   452,     0,     0,     0,   431,   623,     0,     0,   414,
     0,   634,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,   365,     0,     0,     0,     0,     0,
     0,     0,   480,   479,   481,     0,     0,   442,     0,   440,
   443,     0,   429,     0,     0,   424,     0,   411,     0,   427,
   435,   412,     0,     0,     0,     0,     0,   413,     0,     0,
   595,     0,   590,   593,   598,   375,     0,     0,     0,     0,
     0,   482,   392,   361,     0,     0,   439,     0,   415,   437,
     0,     0,   624,     0,     0,     0,   635,   334,     0,     0,
     0,     0,     0,   529,   441,     0,   430,     0,   421,     0,
   419,   410,     0,   425,   428,     0,     0,   591,     0,     0,
     0,     0,   423,     0,   417,   420,   426,     0,   418,
    }, yyDgoto = {
//yyDgoto 212
     1,   356,    69,    70,   731,   650,   196,   211,   451,   588,
   589,   463,   590,   591,   198,    71,    72,    73,    74,    75,
   359,   358,    76,   557,   361,    77,    78,   773,    79,    80,
   452,    81,    82,    83,    84,   458,   231,    86,   685,   465,
   466,   317,   318,    88,    89,    90,    91,   319,   233,   307,
   870,  1052,  1093,   871,   975,    93,   508,   979,   652,   698,
   293,    94,   832,    95,    96,   675,   676,   592,   213,   902,
   235,   593,   594,   566,  1063,  1002,   913,   374,   677,    98,
    99,   286,   481,   722,   864,   325,   236,   320,   653,   564,
   563,   595,   596,   790,   568,   569,   102,   103,   796,  1102,
  1138,   923,   598,  1066,  1067,   599,   331,   511,   289,   104,
   548,  1068,   499,   290,   500,   803,   600,   438,   416,   692,
   572,   570,   105,   106,   709,   237,   214,   215,   601,  1098,
   995,   910,   364,   322,  1071,   274,   512,   791,   792,  1099,
   200,   602,   414,   504,   826,   108,   109,   110,   603,   604,
   605,   606,   701,   425,   924,   275,   276,   113,   114,     2,
   242,   243,   530,   520,   376,   505,   115,   857,   707,   541,
   294,   216,   323,   324,   760,   470,   245,   726,   883,   246,
   884,   736,  1056,   851,   471,   849,   702,   813,   460,   705,
   706,   973,   608,   915,   365,   777,   776,   567,   573,   786,
   571,   784,   770,   769,   898,   994,  1057,  1096,   850,   377,
   378,   459,
    }, yySindex = {
//yySindex 1179
     0,     0, 21016, 22599,     0,     0, 20371, 20767,     0, 23778,
 23778, 19841,     0,     0,  6109, 21411, 21411,     0,     0,     0,
  -161,  -150,     0,     0,     0,     0,    78, 20635,   203,    42,
    85,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0, 23909, 23909,   835, 23909, 23909,    36, 21148,     0, 22071,
 22467,  7633, 23909, 24040, 20503,     0,     0,     0,   267,   323,
     0,     0,     0,     0,     0,     0,     0,   385,   429,     0,
     0,     0,   126,     0,     0,     0,   -79,     0,     0,     0,
     0,     0,     0,     0,     0,     6,     6,  1614,   419,  5586,
     0,   300,    -1,   414,     0,   394,     0,   327,   641,     0,
   590,     0,     0,     0, 24302,   626,     0,   359,     0,   198,
     0,   -31, 21411, 24433, 24564, 20371,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,   353,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,   658,     0,     0, 21280,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,   396,
 21280,     6,   419,    95,   495,   374,   643,   389,    95,     0,
     0,   198,   452,   674,     0, 23778, 23778,     0,     0,  -161,
  -150,     0,     0,     0,     0,   398,   203,     0,     0,     0,
     0,     0,     0,     0,     0,   835,   436,   406,   406,     0,
   625,     0,     0,     0,   471,   -31,     0, 23909, 23909, 23909,
 23909,     0, 23909,  5586,     0,     0,   411,   712,   731,     0,
     0,     0, 17545,     0, 21411, 21543,     0,     0, 19978,     0,
 23778,   -21,     0, 22861, 21016, 21280,     0,   813,   460,   461,
  4010,  4010,   447, 22730,     0, 21148,   450,   198,  1614,     0,
     0,     0,   203,   203, 22730,   449,     0,   225,   244,   411,
     0,   432,   244,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,   498, 24695,   824,     0,   765,
     0,     0,     0,     0,     0,     0,     0,     0,   427,  1098,
  1130,   624,     0,     0,     0,   473,     0,     0,     0,     0,
     0,     0,  3560,  3560, 21280,     0,   757,     0,  3560, 21280,
   768, 23778, 23778, 23778, 23778, 22730, 23778, 23778, 23909, 23909,
 23909, 23909, 23909,     0,     0, 23909, 23909, 23909, 23909, 23909,
 23909, 23909, 23909, 23909, 23909, 23909, 23909, 23909, 23909,     0,
     0, 23909, 23909, 23909, 23909,     0,     0,     0,     0,  6469,
 21411,  6977, 23909,     0,     0,  8502, 24040,     0, 22992, 21148,
  8141,   770, 22992, 24040,     0, 20109,     0,   486,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0, 23778,
    53,     0,   477,   883,     0,     0,     0,   353,     0, 23778,
     0,     0,     0,     0,     0,   588,   589,   447,   793,     0,
 21280,   591,  7485, 21411,  7993, 23909, 23909, 23909, 21280,   -41,
 23123,   596,     0,   127,   127,   520,     0,     0, 18439, 21411,
 18780,     0,     0,     0,     0,   982,     0, 23909, 21675,     0,
     0, 22203,     0,   203,     0,   526,     0, 23909,     0,     0,
   828,   830,   203,   203,    83,     0,     0,     0,     0,     0,
 20767, 23778,  5586,   510,   512,  7485,  7993, 23909, 23909,  1614,
   519,   203,     0,     0, 20240,     0,     0,  1614,     0, 22335,
     0,     0, 22467,     0,     0,     0,     0,     0,   843, 25039,
 21411, 25097, 24695,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,  1201,   259,     0,     0,  3560,  1300,   265,
   644,     0,   523,     0,     0,     0,     0,     0,     0,     0,
     0,     0,  2031,     0,     0,     0,     0,     0,     0,   628,
   629,   876,     0,   203,     0,   898,   900,     0,     0,   902,
     0,     0,     0,   -51,   913, 23909,     0,   897,   913,   203,
   696, 24040,   198,   696, 23909,     0,     0,     0,     0,     0,
     0,     0,   460,  3672,  3672,  3672,  3672,  4518,  4148,  3672,
  3672,  4010,  4010,  1235,  1235,   460,  3489,   460,   460,   388,
   388,  3178,  3178,  5532,  3101,   697,   631,     0,   635,  -150,
     0,     0,     0,   925,   203,   663,     0,   667,  -150,     0,
     0,  3101,     0,     0,  -150,     0,   676,  4062,   899,     0,
     0,   327,   951, 23909,  4062,     0,     0,     0,     0,   942,
   203, 24695,   970,     0,     0,   723,     0,     0,     0,     0,
     0,     0,     0,   419,     0,     0,     0,     0,     0, 25155,
 21411, 25213, 21280,     0,    83,   406, 20899, 20767, 23254,   758,
     0, 23909,   -43,     0,   694,   699,   203,   700,   704,   758,
 23123,   773,   785,   771,     0,     0,     0,     0,     0,     0,
     0,  -150,   203,     0,     0,  -150, 23778, 23909,     0, 23909,
   411,   731,     0,     0,     0,     0, 21807, 22203,     0,     0,
     0,     0,    83,     0,     0,   460,     0, 21016,     0,     0,
   203,   244, 24695,     0,     0,   203,     0,     0,   843,     0,
   513,     0,     0,   230,     0,  1018,  3560,  -131,     0,     0,
   245,     0,  1020,     0,     0,     0,     0,  2031,   886,     0,
  1012,     0,   203,     0,     0,  3650,     0,     0,  3560,     0,
  3621,     0,   614,     0,     0,     0,   -18,     0,     0, 23909,
     0,     0,     0,     0,  4570,     0,     0,     0,  5078,  1004,
     0, 24040, 24040,     0,   486, 21675,   724,   719, 24040, 24040,
     0,   486,     0,     0,   300,   -79, 22730, 23909, 25271, 21411,
 25329, 24040,     0, 23385,     0,   843, 24695, 22730,   705,   198,
 23778, 21280,     0,     0,     0,   203,   805, 20899,     0, 21280,
     0,     0,     0,     0,   735,     0, 21280,   819,     0, 23778,
     0,   821, 23909, 23909,   743, 23909, 23909,   829,   771,     0,
     0, 23516, 21280, 21280, 21280,     0,   127,     0,     0,     0,
  1048,   203,     0,   733,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,   203, 21280, 21280,
     0,     0,     0, 21280,     0,  -130, 21280,   203,  2068,  1052,
  2059,     0,  1055,     0,     0,  1057,  1062,     0,     0,  1063,
     0,  1055,     0,     0,   802,   913,     0, 23909, 23909, 23909,
     0,     0,     0,     0,     0, 24040,     0,     0,     0,     0,
     0,     0,     0,     0,     0,  5586,   631,   635,   203,   663,
   667, 23909,     0,   843,     0,     0, 21280,   198,   848,     0,
     0,     0,   853,   406, 24826,    95,     0,     0, 21280,     0,
     0,    95,     0, 23909,     0, 21280,     0,   383,   855,   859,
     0, 22203,     0,     0,     0,   780,  1067,   871,   774,     0,
     0,  1355,     0,     0,     0,  1092,  2068,   941,     0,  3650,
     0,  3650,     0,  3621,     0,  3650,     0,     0,     0,  5586,
  5586,  5586,  5586,     0,   724,     0,   879,     0,     0,     0,
 21280,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,   836,   962,     0,     0, 21280,     0, 21280,
    28,     0,     0,     0,     0,     0, 21280,     0,     0,  1392,
     0,     0,  1096,   203,  1101,     0,     0,  1105,  1110,     0,
   795,     0,   913, 24957,  1099,   913,  2068,  1092,  2068,  1115,
  1055,  1121,  1055,  1055,     0,   904,     0, 25387, 21411, 25445,
   588,   -43,     0,     0,     0,   906, 21280,     0,   272,     0,
     0,  3650,     0,   811,  3621,     0,   614,     0,  3621,     0,
     0,     0,     0,     0,   856,  1132, 24957,     0,  1092,  2068,
     0,  3650,     0,     0,     0,     0,     0,     0,   203,     0,
     0,     0,     0,     0,   787,  1392,     0,  1133,     0,     0,
  1142,  1146,     0,   913,  1147,  1133,     0,     0, 25503,  1132,
  1092,  1055,     0,     0,     0,  3650,     0,  3650,     0,  3621,
     0,     0,  3650,     0,     0,     0,     0,     0,  1133,  1154,
  1133,  1133,     0,  3650,     0,     0,     0,  1133,     0,
    }, yyRindex = {
//yyRindex 1179
     0,     0,   128,     0,     0,     0,     0,     0,     0,     0,
     0,   938,     0,     0,     0, 11231, 11335,     0,     0,     0,
  5914,  5448, 12639, 12743, 12854, 12991, 24171,     0, 23647,     0,
     0, 13095, 13206, 13343,  6247,  4432, 13447, 13558,  6380, 13695,
     0,     0,     0,     0,     0,     0,     0,   202, 19709,   866,
   863,   299,     0,     0,  1380,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0, 10631,     0,     0,     0, 10742,     0,     0,     0,
     0,     0,     0,     0,     0,   893,   893,    58,  2375,  2990,
 10879,  5182,     0, 13799,     0,  6622,     0, 13910,     0,     0,
     0,     0,     0,     0,   333,     0,     0,     0,     0,    69,
     0, 21939, 11446,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0, 17635, 17697, 17806, 17861,     0,     0,
     0,     0,     0,     0,     0,     0, 17909, 17961, 18014, 18066,
     0,     0,     0, 18120,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,  2947,     0,     0,  1174,  8787,  9119,  9223,
  9334,  9471,  9575,  9686,  3282,  9823,  9927,  3416, 10038,     0,
   202,   893,  1344,     0,     0, 10390,     0,     0,     0,     0,
     0,    68,     0,   399,     0,     0,     0,     0,     0, 10983,
 10175,   698,   751,   903,  1319,     0,   887,  1839,  1985,  2129,
   850,  2564,  2571,  1267,  2702,     0,     0,  1896,  1896,     0,
     0,  2723,     0,     0,     0,     0,     0,  2701,     0,  1756,
     0,     0,     0, 17031,     0,     0, 17335,  1962,  1962,     0,
     0,     0,   894,     0,     0,    49,     0,     0,   894,     0,
     0,     0,     0,     0,    88,    88,     0,     0, 11798, 11094,
 16200, 16264, 14047,     0, 19303,   202,     0,  1451,   753,     0,
     0,   111,   894,   894,     0,     0,     0,   892,   892,     0,
     0,     0,   877,  1673,  1798,  2758,  2894,  3014,  8973,  9004,
  1795,  9898, 10602,  1982, 10954,     0,     0,     0, 11306,   368,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,  3511,     0,     0,     0,     0,
     0,     0,    94,    94,  1174,     0,     0,     0,   258,  1174,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0, 11583,
 11687,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
   145,     0,     0,     0,     0,     0,     0,     0,     0,   202,
   546,   580,     0,     0,     0,    74,     0,  2490,     0,     0,
     0,     0,     0,     0,     0,     0,     0, 18218, 18351,     0,
     0,     0, 19440,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,   364,     0, 10527,     0,     0,
   654, 19033,     0,   145,     0,     0,     0,     0,   648,     0,
     0,     0,     0,     0,     0,     0,     0,  2780,     0,   145,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,   894,     0,     0,     0,   110,     0,     0,
   186,    56,   894,   894,   894,     0,     0,     0,     0,     0,
     0,     0, 16509,     0,     0,     0,     0,     0,     0,  1238,
     0,   894,     0,     0,  1800,   172,     0,   197,     0,   907,
     0,     0,   -33,     0,     0,     0, 11658,     0,   592,     0,
   145,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,   193,
   311,    71,     0,   894,     0,    71,    71,     0,     0,   391,
     0,     0,     0,   261,   391,    21,     0,   499,   391,   894,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0, 11935, 15328, 15451, 15568, 15668, 15759, 16110, 15908,
 16019, 16354, 16445, 14503, 14614, 12039, 14751, 12150, 12287, 14151,
 14262, 14855, 14966,  1195, 15099,     0,  6755,  4807,  8279, 21939,
     0,  4940,     0,   157,   917,  6888,     0,  7263,  5781,     0,
     0, 15211,     0,     0,  8654,     0,  3119, 17119,     0,     0,
     0, 14399,     0,     0,  1555,     0,     0,     0,     0,     0,
   894,     0,   605,     0,     0,  3657,     0, 15797,     0,     0,
     0,     0,     0,   260,     0, 18901,     0,     0,     0,     0,
   145,     0,  1174,     0,   894,  1896,     0,     0,   332,   462,
     0,     0,  1001,     0,  3791,  5315,   917,  3924,  4299,  1001,
     0,     0,     0,     0,     0,     0,     0,     0,     0,  1317,
   955,     0,   917,  2314,  2615, 10279,     0,     0,     0,     0,
 17395,  1962,     0,     0,     0,     0,   207,    75,     0,     0,
     0,     0,   894,     0,     0, 12391,     0,    88,   136,     0,
   894,   892,     0,  2346,  1533,   917,  2966,  3159,   607,     0,
     0,     0,     0,     0,     0,     0,   219,     0,     0,     0,
     0,     0,     0,     0, 19171,     0, 19572,     0,   217,     0,
   231,     0,   894,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0, 17207,     0,     0,     0, 17271,     0,
     0,     0,     0,     0,  4166,   169,  2124,     0,     0,     0,
     0,  4674,     0,     0,  7130,  2338,     0,     0,     0,   145,
     0,     0,  9029,     0,     0,   634,     0,     0,     0,     0,
     0,  1174, 18560, 18692,     0,   917,     0,     0,     0,  1174,
     0,     0,     0,   598,   132,     0,   796,  1001,     0,     0,
     0,     0,     0,     0,  2830,     0,     0,     0,     0,     0,
     0,     0,  1041,   140,   140,   794,     0,     0,     0,     0,
    56,   894,     0,     0,     0,     0,     0,  1216,     0,     0,
     0,     0,     0,     0,     0,     0,     0,    25,  1174,    88,
     0,     0,     0,  1174,     0,     0,    88,   894,     0,   240,
     0,     0,    71,     0,     0,    71,    71,     0,     0,    71,
     0,    71,     0,     0,   261,   391,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0, 16599,  7396,  8412,   917,  7771,
  7904,     0, 17435,   640,     0,     0,  1174,     0,     0,     0,
     0,     0,     0,  1896,     0,     0,     0,     0,   140,     0,
     0,     0,     0,     0,     0,   744,     0,  1001,     0,     0,
     0,   168,     0,     0,     0,     0,  -121,     0,     0,     0,
     0,    44,     0,     0,     0,   249,     0,   254,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0, 16689,
 16753, 16841, 16931,     0, 12502, 17499,     0,     0,     0,     0,
  1174,  1454,  1884,  2259,  2463,  2498,  2525,  2529,  1305,  2545,
  2806,  1943,  3188,     0,     0,  9002,     0,  1174,     0,   654,
  1001,     0,     0,     0,     0,     0,   140,     0,     0,     0,
     0,     0,   156,    44,   156,     0,     0,   194,   156,     0,
     0,     0,   194,   124,   150,   194,     0,   269,     0,   270,
    71,    71,    71,    71,     0,     0,  9160,     0,   145,     0,
   364,  1001,     0,     0,     0,     0,    84,     0,   -16,     0,
     0,     0,     0,     0,   201,     0,     0,     0,     0,     0,
     0,     0,  1752,  8823,     0,   161,     0,     0,   276,     0,
     0,     0,     0,     0,     0,     0,  2461,  1636,   917,  2704,
  3105,     0,     0,     0,     0,     0,     0,   156,     0,     0,
   156,   156,     0,   194,   156,   156,     0,     0,     0,   188,
   279,    71,  2406,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,  9000,   720,     0,   156,   156,
   156,   156,     0,     0,     0,     0,     0,   156,     0,
    }, yyGindex = {
//yyGindex 212
     0,     0,    14,     0,  -395,     0,  -349,     9,  -285,  -288,
     0,   816,     0,  -342,  1164,     0,     0,  1208,     0,     0,
  1003,  1226,     0,  -253,     0,     0,     0,   717,     0,    20,
  1173,  -269,   -36,     0,   101,   444,   467,     5,     0,   291,
  -282,     0,   112,   142,  2528,    22,   -11,   772,    29,    -2,
  -485,     0,     0,   211,     0,     0,   646,     0,    16,     0,
    11,   152,   653,     0,     0,  -362,   556,  -660,     0,     0,
   159,  -335,   737,     0,     0,     0,   533,   -84,  -341,   -70,
   -23,   979,  -456,   274,     0,     0,  1682,  1078,   633,     0,
     0,   170,   522,   216,     0,     0,     0,     0,   337,  2746,
   321,  -311,   529,   228,     0,     0,     0,    37,  -446,     0,
  -333,   232,  -266,  -423,     0,  -579,   285,   -65,   503,  -470,
   651,   933,  1338,     3,   277,  1866,     0,   -17,  -709,     0,
  -812,     0,     0,  -208,  -920,     0,  -360,  -772,   572,   233,
     0,  -943,  1273,   857,  -633,  -247,     0,    51,  -720,   166,
  2162,  -262,   -89,     0,  -203,  1100,  1446,     0,     0,     0,
    54,    -9,     0,     0,    99,    -8,     0,     0,     0,  -284,
     0,     0,     0,     0,     0,  -222,     0,  -442,     0,     0,
     0,     0,     0,     0,    24,     0,     0,   761,     0,     0,
     0,     0,  2321,     0,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     0,     0,
    };
    protected static final short[] yyTable = YyTables.yyTable();
    protected static final short[] yyCheck = YyTables.yyCheck();

  /** maps symbol value to printable name.
      @see #yyExpecting
    */
  protected static final String[] yyNames = {
    "end-of-file",null,null,null,null,null,null,null,null,null,"'\\n'",
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,"' '",null,null,null,null,null,
    null,null,"'('",null,null,null,"','",null,null,null,null,null,null,
    null,null,null,null,null,null,null,"':'","';'",null,"'='",null,"'?'",
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,
    "'['",null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,
    "keyword_class","keyword_module","keyword_def","keyword_undef",
    "keyword_begin","keyword_rescue","keyword_ensure","keyword_end",
    "keyword_if","keyword_unless","keyword_then","keyword_elsif",
    "keyword_else","keyword_case","keyword_when","keyword_while",
    "keyword_until","keyword_for","keyword_break","keyword_next",
    "keyword_redo","keyword_retry","keyword_in","keyword_do",
    "keyword_do_cond","keyword_do_block","keyword_return","keyword_yield",
    "keyword_super","keyword_self","keyword_nil","keyword_true",
    "keyword_false","keyword_and","keyword_or","keyword_not",
    "modifier_if","modifier_unless","modifier_while","modifier_until",
    "modifier_rescue","keyword_alias","keyword_defined","keyword_BEGIN",
    "keyword_END","keyword__LINE__","keyword__FILE__",
    "keyword__ENCODING__","keyword_do_lambda","tIDENTIFIER","tFID",
    "tGVAR","tIVAR","tCONSTANT","tCVAR","tLABEL","tCHAR","unary+",
"unary-","tUMINUS_NUM","'**'","'<=>'","'=='","'==='","'!='","'>='",
"'<='","'&&'","'||'","'=~'","'!~'","'.'","'..'","'...'",
    "tBDOT2","tBDOT3","'[]'","'[]='","'<<'","'>>'","'&.'",
"'::'","':: at EXPR_BEG'","tOP_ASGN","'=>'","'('","'( arg'",
"')'","'['","'{'","'{ arg'","'['","'[ args'",
"'*'","'*'","'&'","'&'","'~'","'%'","'/'",
"'+'","'-'","'<'","'>'","'|'","'!'","'^'","'{'",
"'}'","'`'","':'","tSTRING_BEG","tXSTRING_BEG",
    "tREGEXP_BEG","tWORDS_BEG","tQWORDS_BEG","tSTRING_DBEG",
    "tSTRING_DVAR","tSTRING_END","'->'","tLAMBEG","tNTH_REF",
    "tBACK_REF","tSTRING_CONTENT","tINTEGER","tIMAGINARY","tFLOAT",
    "tRATIONAL","tREGEXP_END","tSYMBOLS_BEG","tQSYMBOLS_BEG","'**'",
    "tSTRING_DEND","tLABEL_END","tLOWEST",
    };

  /** printable rules for debugging.
    */
  protected static final String [] yyRule = {
    "$accept : program",
    "$$1 :",
    "program : $$1 top_compstmt",
    "top_compstmt : top_stmts opt_terms",
    "top_stmts : none",
    "top_stmts : top_stmt",
    "top_stmts : top_stmts terms top_stmt",
    "top_stmts : error top_stmt",
    "top_stmt : stmt",
    "top_stmt : keyword_BEGIN tLCURLY top_compstmt tRCURLY",
    "bodystmt : compstmt opt_rescue opt_else opt_ensure",
    "compstmt : stmts opt_terms",
    "stmts : none",
    "stmts : stmt_or_begin",
    "stmts : stmts terms stmt_or_begin",
    "stmts : error stmt",
    "stmt_or_begin : stmt",
    "$$2 :",
    "stmt_or_begin : keyword_begin $$2 tLCURLY top_compstmt tRCURLY",
    "$$3 :",
    "stmt : keyword_alias fitem $$3 fitem",
    "stmt : keyword_alias tGVAR tGVAR",
    "stmt : keyword_alias tGVAR tBACK_REF",
    "stmt : keyword_alias tGVAR tNTH_REF",
    "stmt : keyword_undef undef_list",
    "stmt : stmt modifier_if expr_value",
    "stmt : stmt modifier_unless expr_value",
    "stmt : stmt modifier_while expr_value",
    "stmt : stmt modifier_until expr_value",
    "stmt : stmt modifier_rescue stmt",
    "stmt : keyword_END tLCURLY compstmt tRCURLY",
    "stmt : command_asgn",
    "stmt : mlhs '=' command_call",
    "stmt : lhs '=' mrhs",
    "stmt : mlhs '=' mrhs_arg modifier_rescue stmt",
    "stmt : mlhs '=' mrhs_arg",
    "stmt : expr",
    "command_asgn : lhs '=' command_rhs",
    "command_asgn : var_lhs tOP_ASGN command_rhs",
    "command_asgn : primary_value '[' opt_call_args rbracket tOP_ASGN command_rhs",
    "command_asgn : primary_value call_op tIDENTIFIER tOP_ASGN command_rhs",
    "command_asgn : primary_value call_op tCONSTANT tOP_ASGN command_rhs",
    "command_asgn : primary_value tCOLON2 tCONSTANT tOP_ASGN command_rhs",
    "command_asgn : primary_value tCOLON2 tIDENTIFIER tOP_ASGN command_rhs",
    "command_asgn : defn_head f_opt_paren_args '=' command",
    "command_asgn : backref tOP_ASGN command_rhs",
    "command_rhs : command_call",
    "command_rhs : command_call modifier_rescue stmt",
    "command_rhs : command_asgn",
    "expr : command_call",
    "expr : expr keyword_and expr",
    "expr : expr keyword_or expr",
    "expr : keyword_not opt_nl expr",
    "expr : tBANG command_call",
    "expr : arg",
    "def_name : fname",
    "defn_head : k_def def_name",
    "$$4 :",
    "defs_head : k_def singleton dot_or_colon $$4 def_name",
    "expr_value : expr",
    "command_call : command",
    "command_call : block_command",
    "block_command : block_call",
    "block_command : block_call call_op2 operation2 command_args",
    "cmd_brace_block : tLBRACE_ARG brace_body tRCURLY",
    "fcall : operation",
    "command : fcall command_args",
    "command : fcall command_args cmd_brace_block",
    "command : primary_value call_op operation2 command_args",
    "command : primary_value call_op operation2 command_args cmd_brace_block",
    "command : primary_value tCOLON2 operation2 command_args",
    "command : primary_value tCOLON2 operation2 command_args cmd_brace_block",
    "command : keyword_super command_args",
    "command : keyword_yield command_args",
    "command : k_return call_args",
    "command : keyword_break call_args",
    "command : keyword_next call_args",
    "mlhs : mlhs_basic",
    "mlhs : tLPAREN mlhs_inner rparen",
    "mlhs_inner : mlhs_basic",
    "mlhs_inner : tLPAREN mlhs_inner rparen",
    "mlhs_basic : mlhs_head",
    "mlhs_basic : mlhs_head mlhs_item",
    "mlhs_basic : mlhs_head tSTAR mlhs_node",
    "mlhs_basic : mlhs_head tSTAR mlhs_node ',' mlhs_post",
    "mlhs_basic : mlhs_head tSTAR",
    "mlhs_basic : mlhs_head tSTAR ',' mlhs_post",
    "mlhs_basic : tSTAR mlhs_node",
    "mlhs_basic : tSTAR mlhs_node ',' mlhs_post",
    "mlhs_basic : tSTAR",
    "mlhs_basic : tSTAR ',' mlhs_post",
    "mlhs_item : mlhs_node",
    "mlhs_item : tLPAREN mlhs_inner rparen",
    "mlhs_head : mlhs_item ','",
    "mlhs_head : mlhs_head mlhs_item ','",
    "mlhs_post : mlhs_item",
    "mlhs_post : mlhs_post ',' mlhs_item",
    "mlhs_node : tIDENTIFIER",
    "mlhs_node : tIVAR",
    "mlhs_node : tGVAR",
    "mlhs_node : tCONSTANT",
    "mlhs_node : tCVAR",
    "mlhs_node : keyword_nil",
    "mlhs_node : keyword_self",
    "mlhs_node : keyword_true",
    "mlhs_node : keyword_false",
    "mlhs_node : keyword__FILE__",
    "mlhs_node : keyword__LINE__",
    "mlhs_node : keyword__ENCODING__",
    "mlhs_node : primary_value '[' opt_call_args rbracket",
    "mlhs_node : primary_value call_op tIDENTIFIER",
    "mlhs_node : primary_value tCOLON2 tIDENTIFIER",
    "mlhs_node : primary_value call_op tCONSTANT",
    "mlhs_node : primary_value tCOLON2 tCONSTANT",
    "mlhs_node : tCOLON3 tCONSTANT",
    "mlhs_node : backref",
    "lhs : tIDENTIFIER",
    "lhs : tIVAR",
    "lhs : tGVAR",
    "lhs : tCONSTANT",
    "lhs : tCVAR",
    "lhs : keyword_nil",
    "lhs : keyword_self",
    "lhs : keyword_true",
    "lhs : keyword_false",
    "lhs : keyword__FILE__",
    "lhs : keyword__LINE__",
    "lhs : keyword__ENCODING__",
    "lhs : primary_value '[' opt_call_args rbracket",
    "lhs : primary_value call_op tIDENTIFIER",
    "lhs : primary_value tCOLON2 tIDENTIFIER",
    "lhs : primary_value call_op tCONSTANT",
    "lhs : primary_value tCOLON2 tCONSTANT",
    "lhs : tCOLON3 tCONSTANT",
    "lhs : backref",
    "cname : tIDENTIFIER",
    "cname : tCONSTANT",
    "cpath : tCOLON3 cname",
    "cpath : cname",
    "cpath : primary_value tCOLON2 cname",
    "fname : tIDENTIFIER",
    "fname : tCONSTANT",
    "fname : tFID",
    "fname : op",
    "fname : reswords",
    "fsym : fname",
    "fsym : symbol",
    "fitem : fsym",
    "fitem : dsym",
    "undef_list : fitem",
    "$$5 :",
    "undef_list : undef_list ',' $$5 fitem",
    "op : tPIPE",
    "op : tCARET",
    "op : tAMPER2",
    "op : tCMP",
    "op : tEQ",
    "op : tEQQ",
    "op : tMATCH",
    "op : tNMATCH",
    "op : tGT",
    "op : tGEQ",
    "op : tLT",
    "op : tLEQ",
    "op : tNEQ",
    "op : tLSHFT",
    "op : tRSHFT",
    "op : tDSTAR",
    "op : tPLUS",
    "op : tMINUS",
    "op : tSTAR2",
    "op : tSTAR",
    "op : tDIVIDE",
    "op : tPERCENT",
    "op : tPOW",
    "op : tBANG",
    "op : tTILDE",
    "op : tUPLUS",
    "op : tUMINUS",
    "op : tAREF",
    "op : tASET",
    "op : tBACK_REF2",
    "reswords : keyword__LINE__",
    "reswords : keyword__FILE__",
    "reswords : keyword__ENCODING__",
    "reswords : keyword_BEGIN",
    "reswords : keyword_END",
    "reswords : keyword_alias",
    "reswords : keyword_and",
    "reswords : keyword_begin",
    "reswords : keyword_break",
    "reswords : keyword_case",
    "reswords : keyword_class",
    "reswords : keyword_def",
    "reswords : keyword_defined",
    "reswords : keyword_do",
    "reswords : keyword_else",
    "reswords : keyword_elsif",
    "reswords : keyword_end",
    "reswords : keyword_ensure",
    "reswords : keyword_false",
    "reswords : keyword_for",
    "reswords : keyword_in",
    "reswords : keyword_module",
    "reswords : keyword_next",
    "reswords : keyword_nil",
    "reswords : keyword_not",
    "reswords : keyword_or",
    "reswords : keyword_redo",
    "reswords : keyword_rescue",
    "reswords : keyword_retry",
    "reswords : keyword_return",
    "reswords : keyword_self",
    "reswords : keyword_super",
    "reswords : keyword_then",
    "reswords : keyword_true",
    "reswords : keyword_undef",
    "reswords : keyword_when",
    "reswords : keyword_yield",
    "reswords : keyword_if",
    "reswords : keyword_unless",
    "reswords : keyword_while",
    "reswords : keyword_until",
    "reswords : modifier_rescue",
    "arg : lhs '=' arg_rhs",
    "arg : var_lhs tOP_ASGN arg_rhs",
    "arg : primary_value '[' opt_call_args rbracket tOP_ASGN arg",
    "arg : primary_value call_op tIDENTIFIER tOP_ASGN arg_rhs",
    "arg : primary_value call_op tCONSTANT tOP_ASGN arg_rhs",
    "arg : primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg_rhs",
    "arg : primary_value tCOLON2 tCONSTANT tOP_ASGN arg_rhs",
    "arg : tCOLON3 tCONSTANT tOP_ASGN arg_rhs",
    "arg : backref tOP_ASGN arg_rhs",
    "arg : arg tDOT2 arg",
    "arg : arg tDOT2",
    "arg : tBDOT2 arg",
    "arg : arg tDOT3 arg",
    "arg : arg tDOT3",
    "arg : tBDOT3 arg",
    "arg : arg tPLUS arg",
    "arg : arg tMINUS arg",
    "arg : arg tSTAR2 arg",
    "arg : arg tDIVIDE arg",
    "arg : arg tPERCENT arg",
    "arg : arg tPOW arg",
    "arg : tUMINUS_NUM simple_numeric tPOW arg",
    "arg : tUPLUS arg",
    "arg : tUMINUS arg",
    "arg : arg tPIPE arg",
    "arg : arg tCARET arg",
    "arg : arg tAMPER2 arg",
    "arg : arg tCMP arg",
    "arg : rel_expr",
    "arg : arg tEQ arg",
    "arg : arg tEQQ arg",
    "arg : arg tNEQ arg",
    "arg : arg tMATCH arg",
    "arg : arg tNMATCH arg",
    "arg : tBANG arg",
    "arg : tTILDE arg",
    "arg : arg tLSHFT arg",
    "arg : arg tRSHFT arg",
    "arg : arg tANDOP arg",
    "arg : arg tOROP arg",
    "arg : keyword_defined opt_nl arg",
    "arg : arg '?' arg opt_nl ':' arg",
    "arg : defn_head f_opt_paren_args '=' arg",
    "arg : defn_head f_opt_paren_args '=' arg modifier_rescue arg",
    "arg : defs_head f_opt_paren_args '=' arg",
    "arg : defs_head f_opt_paren_args '=' arg modifier_rescue arg",
    "arg : primary",
    "relop : tGT",
    "relop : tLT",
    "relop : tGEQ",
    "relop : tLEQ",
    "rel_expr : arg relop arg",
    "rel_expr : rel_expr relop arg",
    "arg_value : arg",
    "aref_args : none",
    "aref_args : args trailer",
    "aref_args : args ',' assocs trailer",
    "aref_args : assocs trailer",
    "arg_rhs : arg",
    "arg_rhs : arg modifier_rescue arg",
    "paren_args : tLPAREN2 opt_call_args rparen",
    "paren_args : tLPAREN2 args ',' args_forward rparen",
    "paren_args : tLPAREN2 args_forward rparen",
    "opt_paren_args : none",
    "opt_paren_args : paren_args",
    "opt_call_args : none",
    "opt_call_args : call_args",
    "opt_call_args : args ','",
    "opt_call_args : args ',' assocs ','",
    "opt_call_args : assocs ','",
    "call_args : command",
    "call_args : args opt_block_arg",
    "call_args : assocs opt_block_arg",
    "call_args : args ',' assocs opt_block_arg",
    "call_args : block_arg",
    "$$6 :",
    "command_args : $$6 call_args",
    "block_arg : tAMPER arg_value",
    "block_arg : tAMPER",
    "opt_block_arg : ',' block_arg",
    "opt_block_arg : none_block_pass",
    "args : arg_value",
    "args : tSTAR arg_value",
    "args : args ',' arg_value",
    "args : args ',' tSTAR arg_value",
    "mrhs_arg : mrhs",
    "mrhs_arg : arg_value",
    "mrhs : args ',' arg_value",
    "mrhs : args ',' tSTAR arg_value",
    "mrhs : tSTAR arg_value",
    "primary : literal",
    "primary : strings",
    "primary : xstring",
    "primary : regexp",
    "primary : words",
    "primary : qwords",
    "primary : symbols",
    "primary : qsymbols",
    "primary : var_ref",
    "primary : backref",
    "primary : tFID",
    "$$7 :",
    "primary : keyword_begin $$7 bodystmt keyword_end",
    "$$8 :",
    "primary : tLPAREN_ARG $$8 rparen",
    "$$9 :",
    "$$10 :",
    "primary : tLPAREN_ARG $$9 stmt $$10 rparen",
    "primary : tLPAREN compstmt tRPAREN",
    "primary : primary_value tCOLON2 tCONSTANT",
    "primary : tCOLON3 tCONSTANT",
    "primary : tLBRACK aref_args tRBRACK",
    "primary : tLBRACE assoc_list tRCURLY",
    "primary : k_return",
    "primary : keyword_yield tLPAREN2 call_args rparen",
    "primary : keyword_yield tLPAREN2 rparen",
    "primary : keyword_yield",
    "primary : keyword_defined opt_nl tLPAREN2 expr rparen",
    "primary : keyword_not tLPAREN2 expr rparen",
    "primary : keyword_not tLPAREN2 rparen",
    "primary : fcall brace_block",
    "primary : method_call",
    "primary : method_call brace_block",
    "primary : tLAMBDA lambda",
    "primary : keyword_if expr_value then compstmt if_tail keyword_end",
    "primary : keyword_unless expr_value then compstmt opt_else keyword_end",
    "$$11 :",
    "$$12 :",
    "primary : keyword_while $$11 expr_value do $$12 compstmt keyword_end",
    "$$13 :",
    "$$14 :",
    "primary : keyword_until $$13 expr_value do $$14 compstmt keyword_end",
    "primary : keyword_case expr_value opt_terms case_body keyword_end",
    "primary : keyword_case opt_terms case_body keyword_end",
    "primary : keyword_case expr_value opt_terms p_case_body keyword_end",
    "$$15 :",
    "$$16 :",
    "primary : keyword_for for_var keyword_in $$15 expr_value do $$16 compstmt keyword_end",
    "$$17 :",
    "primary : k_class cpath superclass $$17 bodystmt keyword_end",
    "$$18 :",
    "primary : k_class tLSHFT expr $$18 term bodystmt keyword_end",
    "$$19 :",
    "primary : k_module cpath $$19 bodystmt keyword_end",
    "primary : defn_head f_arglist bodystmt k_end",
    "primary : defs_head f_arglist bodystmt k_end",
    "$$20 :",
    "$$21 :",
    "primary : keyword_def fname $$20 $$21 f_arglist bodystmt keyword_end",
    "$$22 :",
    "$$23 :",
    "primary : keyword_def singleton dot_or_colon $$22 fname $$23 f_arglist bodystmt keyword_end",
    "primary : keyword_break",
    "primary : keyword_next",
    "primary : keyword_redo",
    "primary : keyword_retry",
    "primary_value : primary",
    "k_class : keyword_class",
    "k_module : keyword_module",
    "k_def : keyword_def",
    "k_end : keyword_end",
    "k_return : keyword_return",
    "then : term",
    "then : keyword_then",
    "then : term keyword_then",
    "do : term",
    "do : keyword_do_cond",
    "if_tail : opt_else",
    "if_tail : keyword_elsif expr_value then compstmt if_tail",
    "opt_else : none",
    "opt_else : keyword_else compstmt",
    "for_var : lhs",
    "for_var : mlhs",
    "f_marg : f_norm_arg",
    "f_marg : tLPAREN f_margs rparen",
    "f_marg_list : f_marg",
    "f_marg_list : f_marg_list ',' f_marg",
    "f_margs : f_marg_list",
    "f_margs : f_marg_list ',' tSTAR f_norm_arg",
    "f_margs : f_marg_list ',' tSTAR f_norm_arg ',' f_marg_list",
    "f_margs : f_marg_list ',' tSTAR",
    "f_margs : f_marg_list ',' tSTAR ',' f_marg_list",
    "f_margs : tSTAR f_norm_arg",
    "f_margs : tSTAR f_norm_arg ',' f_marg_list",
    "f_margs : tSTAR",
    "f_margs : tSTAR ',' f_marg_list",
    "block_args_tail : f_block_kwarg ',' f_kwrest opt_f_block_arg",
    "block_args_tail : f_block_kwarg opt_f_block_arg",
    "block_args_tail : f_kwrest opt_f_block_arg",
    "block_args_tail : f_no_kwarg opt_f_block_arg",
    "block_args_tail : f_block_arg",
    "opt_block_args_tail : ',' block_args_tail",
    "opt_block_args_tail :",
    "block_param : f_arg ',' f_block_optarg ',' f_rest_arg opt_block_args_tail",
    "block_param : f_arg ',' f_block_optarg ',' f_rest_arg ',' f_arg opt_block_args_tail",
    "block_param : f_arg ',' f_block_optarg opt_block_args_tail",
    "block_param : f_arg ',' f_block_optarg ',' f_arg opt_block_args_tail",
    "block_param : f_arg ',' f_rest_arg opt_block_args_tail",
    "block_param : f_arg ','",
    "block_param : f_arg ',' f_rest_arg ',' f_arg opt_block_args_tail",
    "block_param : f_arg opt_block_args_tail",
    "block_param : f_block_optarg ',' f_rest_arg opt_block_args_tail",
    "block_param : f_block_optarg ',' f_rest_arg ',' f_arg opt_block_args_tail",
    "block_param : f_block_optarg opt_block_args_tail",
    "block_param : f_block_optarg ',' f_arg opt_block_args_tail",
    "block_param : f_rest_arg opt_block_args_tail",
    "block_param : f_rest_arg ',' f_arg opt_block_args_tail",
    "block_param : block_args_tail",
    "opt_block_param : none",
    "$$24 :",
    "opt_block_param : $$24 block_param_def",
    "block_param_def : tPIPE opt_bv_decl tPIPE",
    "block_param_def : tOROP",
    "block_param_def : tPIPE block_param opt_bv_decl tPIPE",
    "opt_bv_decl : opt_nl",
    "opt_bv_decl : opt_nl ';' bv_decls opt_nl",
    "bv_decls : bvar",
    "bv_decls : bv_decls ',' bvar",
    "bvar : tIDENTIFIER",
    "bvar : f_bad_arg",
    "$$25 :",
    "$$26 :",
    "lambda : $$25 f_larglist $$26 lambda_body",
    "$$27 :",
    "f_larglist : tLPAREN2 $$27 f_args opt_bv_decl tRPAREN",
    "$$28 :",
    "f_larglist : $$28 f_args_any",
    "f_larglist :",
    "lambda_body : tLAMBEG compstmt tRCURLY",
    "lambda_body : keyword_do_lambda bodystmt keyword_end",
    "do_block : keyword_do_block do_body keyword_end",
    "block_call : command do_block",
    "block_call : block_call call_op2 operation2 opt_paren_args",
    "block_call : block_call call_op2 operation2 opt_paren_args brace_block",
    "block_call : block_call call_op2 operation2 command_args do_block",
    "method_call : fcall paren_args",
    "method_call : primary_value call_op operation2 opt_paren_args",
    "method_call : primary_value tCOLON2 operation2 paren_args",
    "method_call : primary_value tCOLON2 operation3",
    "method_call : primary_value call_op paren_args",
    "method_call : primary_value tCOLON2 paren_args",
    "method_call : keyword_super paren_args",
    "method_call : keyword_super",
    "method_call : primary_value '[' opt_call_args rbracket",
    "brace_block : tLCURLY brace_body tRCURLY",
    "brace_block : keyword_do do_body keyword_end",
    "$$29 :",
    "$$30 :",
    "brace_body : $$29 $$30 opt_block_param compstmt",
    "$$31 :",
    "$$32 :",
    "do_body : $$31 $$32 opt_block_param bodystmt",
    "case_body : keyword_when args then compstmt cases",
    "cases : opt_else",
    "cases : case_body",
    "p_case_body : keyword_in args then compstmt p_cases",
    "p_cases : opt_else",
    "p_cases : p_case_body",
    "opt_rescue : keyword_rescue exc_list exc_var then compstmt opt_rescue",
    "opt_rescue :",
    "exc_list : arg_value",
    "exc_list : mrhs",
    "exc_list : none",
    "exc_var : tASSOC lhs",
    "exc_var : none",
    "opt_ensure : keyword_ensure compstmt",
    "opt_ensure : none",
    "literal : numeric",
    "literal : symbol",
    "literal : dsym",
    "strings : string",
    "string : tCHAR",
    "string : string1",
    "string : string string1",
    "string1 : tSTRING_BEG string_contents tSTRING_END",
    "xstring : tXSTRING_BEG xstring_contents tSTRING_END",
    "regexp : tREGEXP_BEG regexp_contents tREGEXP_END",
    "words : tWORDS_BEG ' ' word_list tSTRING_END",
    "word_list :",
    "word_list : word_list word ' '",
    "word : string_content",
    "word : word string_content",
    "symbols : tSYMBOLS_BEG ' ' symbol_list tSTRING_END",
    "symbol_list :",
    "symbol_list : symbol_list word ' '",
    "qwords : tQWORDS_BEG ' ' qword_list tSTRING_END",
    "qsymbols : tQSYMBOLS_BEG ' ' qsym_list tSTRING_END",
    "qword_list :",
    "qword_list : qword_list tSTRING_CONTENT ' '",
    "qsym_list :",
    "qsym_list : qsym_list tSTRING_CONTENT ' '",
    "string_contents :",
    "string_contents : string_contents string_content",
    "xstring_contents :",
    "xstring_contents : xstring_contents string_content",
    "regexp_contents :",
    "regexp_contents : regexp_contents string_content",
    "string_content : tSTRING_CONTENT",
    "$$33 :",
    "string_content : tSTRING_DVAR $$33 string_dvar",
    "$$34 :",
    "$$35 :",
    "$$36 :",
    "$$37 :",
    "$$38 :",
    "string_content : tSTRING_DBEG $$34 $$35 $$36 $$37 $$38 compstmt tSTRING_DEND",
    "string_dvar : tGVAR",
    "string_dvar : tIVAR",
    "string_dvar : tCVAR",
    "string_dvar : backref",
    "symbol : tSYMBEG sym",
    "sym : fname",
    "sym : tIVAR",
    "sym : tGVAR",
    "sym : tCVAR",
    "dsym : tSYMBEG xstring_contents tSTRING_END",
    "numeric : simple_numeric",
    "numeric : tUMINUS_NUM simple_numeric",
    "simple_numeric : tINTEGER",
    "simple_numeric : tFLOAT",
    "simple_numeric : tRATIONAL",
    "simple_numeric : tIMAGINARY",
    "var_ref : tIDENTIFIER",
    "var_ref : tIVAR",
    "var_ref : tGVAR",
    "var_ref : tCONSTANT",
    "var_ref : tCVAR",
    "var_ref : keyword_nil",
    "var_ref : keyword_self",
    "var_ref : keyword_true",
    "var_ref : keyword_false",
    "var_ref : keyword__FILE__",
    "var_ref : keyword__LINE__",
    "var_ref : keyword__ENCODING__",
    "var_lhs : tIDENTIFIER",
    "var_lhs : tIVAR",
    "var_lhs : tGVAR",
    "var_lhs : tCONSTANT",
    "var_lhs : tCVAR",
    "var_lhs : keyword_nil",
    "var_lhs : keyword_self",
    "var_lhs : keyword_true",
    "var_lhs : keyword_false",
    "var_lhs : keyword__FILE__",
    "var_lhs : keyword__LINE__",
    "var_lhs : keyword__ENCODING__",
    "backref : tNTH_REF",
    "backref : tBACK_REF",
    "$$39 :",
    "superclass : tLT $$39 expr_value term",
    "superclass :",
    "f_opt_paren_args : f_paren_args",
    "f_opt_paren_args : none",
    "f_paren_args : '(' f_args rparen",
    "f_arglist : tLPAREN2 f_args rparen",
    "$$40 :",
    "f_arglist : $$40 f_args term",
    "args_tail : f_kwarg ',' f_kwrest opt_f_block_arg",
    "args_tail : f_kwarg opt_f_block_arg",
    "args_tail : f_kwrest opt_f_block_arg",
    "args_tail : f_no_kwarg opt_f_block_arg",
    "args_tail : f_block_arg",
    "opt_args_tail : ',' args_tail",
    "opt_args_tail :",
    "f_args : f_args_any",
    "f_args :",
    "f_args_any : f_arg ',' f_optarg ',' f_rest_arg opt_args_tail",
    "f_args_any : f_arg ',' f_optarg ',' f_rest_arg ',' f_arg opt_args_tail",
    "f_args_any : f_arg ',' f_optarg opt_args_tail",
    "f_args_any : f_arg ',' f_optarg ',' f_arg opt_args_tail",
    "f_args_any : f_arg ',' f_rest_arg opt_args_tail",
    "f_args_any : f_arg ',' f_rest_arg ',' f_arg opt_args_tail",
    "f_args_any : f_arg opt_args_tail",
    "f_args_any : f_optarg ',' f_rest_arg opt_args_tail",
    "f_args_any : f_optarg ',' f_rest_arg ',' f_arg opt_args_tail",
    "f_args_any : f_optarg opt_args_tail",
    "f_args_any : f_optarg ',' f_arg opt_args_tail",
    "f_args_any : f_rest_arg opt_args_tail",
    "f_args_any : f_rest_arg ',' f_arg opt_args_tail",
    "f_args_any : args_tail",
    "f_args_any : f_arg ',' args_forward",
    "f_args_any : args_forward",
    "args_forward : tBDOT3",
    "f_bad_arg : tCONSTANT",
    "f_bad_arg : tIVAR",
    "f_bad_arg : tGVAR",
    "f_bad_arg : tCVAR",
    "f_norm_arg : f_bad_arg",
    "f_norm_arg : tIDENTIFIER",
    "f_arg_asgn : f_norm_arg",
    "f_arg_item : f_arg_asgn",
    "f_arg_item : tLPAREN f_margs rparen",
    "f_arg : f_arg_item",
    "f_arg : f_arg ',' f_arg_item",
    "f_label : tLABEL",
    "f_kw : f_label arg_value",
    "f_kw : f_label",
    "f_block_kw : f_label primary_value",
    "f_block_kw : f_label",
    "f_block_kwarg : f_block_kw",
    "f_block_kwarg : f_block_kwarg ',' f_block_kw",
    "f_kwarg : f_kw",
    "f_kwarg : f_kwarg ',' f_kw",
    "kwrest_mark : tPOW",
    "kwrest_mark : tDSTAR",
    "f_no_kwarg : kwrest_mark keyword_nil",
    "f_kwrest : kwrest_mark tIDENTIFIER",
    "f_kwrest : kwrest_mark",
    "f_opt : f_arg_asgn '=' arg_value",
    "f_block_opt : f_arg_asgn '=' primary_value",
    "f_block_optarg : f_block_opt",
    "f_block_optarg : f_block_optarg ',' f_block_opt",
    "f_optarg : f_opt",
    "f_optarg : f_optarg ',' f_opt",
    "restarg_mark : tSTAR2",
    "restarg_mark : tSTAR",
    "f_rest_arg : restarg_mark tIDENTIFIER",
    "f_rest_arg : restarg_mark",
    "blkarg_mark : tAMPER2",
    "blkarg_mark : tAMPER",
    "f_block_arg : blkarg_mark tIDENTIFIER",
    "f_block_arg : blkarg_mark",
    "opt_f_block_arg : ',' f_block_arg",
    "opt_f_block_arg :",
    "singleton : var_ref",
    "$$41 :",
    "singleton : tLPAREN2 $$41 expr rparen",
    "assoc_list : none",
    "assoc_list : assocs trailer",
    "assocs : assoc",
    "assocs : assocs ',' assoc",
    "assoc : arg_value tASSOC arg_value",
    "assoc : tLABEL arg_value",
    "assoc : tLABEL",
    "assoc : tSTRING_BEG string_contents tLABEL_END arg_value",
    "assoc : tDSTAR arg_value",
    "operation : tIDENTIFIER",
    "operation : tCONSTANT",
    "operation : tFID",
    "operation2 : tIDENTIFIER",
    "operation2 : tCONSTANT",
    "operation2 : tFID",
    "operation2 : op",
    "operation3 : tIDENTIFIER",
    "operation3 : tFID",
    "operation3 : op",
    "dot_or_colon : tDOT",
    "dot_or_colon : tCOLON2",
    "call_op : tDOT",
    "call_op : tANDDOT",
    "call_op2 : call_op",
    "call_op2 : tCOLON2",
    "opt_terms :",
    "opt_terms : terms",
    "opt_nl :",
    "opt_nl : '\\n'",
    "rparen : opt_nl tRPAREN",
    "rbracket : opt_nl tRBRACK",
    "trailer :",
    "trailer : '\\n'",
    "trailer : ','",
    "term : ';'",
    "term : '\\n'",
    "terms : term",
    "terms : terms ';'",
    "none :",
    "none_block_pass :",
    };

  protected org.truffleruby.parser.parser.YYDebug yydebug;

  /** index-checked interface to {@link #yyNames}.
      @param token single character or <tt>%token</tt> value.
      @return token name or <tt>[illegal]</tt> or <tt>[unknown]</tt>.
    */
  public static String yyName (int token) {
    if (token < 0 || token > yyNames.length) return "[illegal]";
    String name;
    if ((name = yyNames[token]) != null) return name;
    return "[unknown]";
  }


  /** computes list of expected tokens on error by tracing the tables.
      @param state for which to compute the list.
      @return list of token names.
    */
  protected String[] yyExpecting (int state) {
    int token, n, len = 0;
    boolean[] ok = new boolean[yyNames.length];

    if ((n = yySindex[state]) != 0)
      for (token = n < 0 ? -n : 0;
           token < yyNames.length && n+token < yyTable.length; ++ token)
        if (yyCheck[n+token] == token && !ok[token] && yyNames[token] != null) {
          ++ len;
          ok[token] = true;
        }
    if ((n = yyRindex[state]) != 0)
      for (token = n < 0 ? -n : 0;
           token < yyNames.length && n+token < yyTable.length; ++ token)
        if (yyCheck[n+token] == token && !ok[token] && yyNames[token] != null) {
          ++ len;
          ok[token] = true;
        }

    String result[] = new String[len];
    for (n = token = 0; n < len;  ++ token)
      if (ok[token]) result[n++] = yyNames[token];
    return result;
  }

  /** the generated parser, with debugging messages.
      Maintains a dynamic state and value stack.
      @param yyLex scanner.
      @return result of the last reduction, if any.
    */
  public Object yyparse (RubyLexer yyLex, Object ayydebug) {
    this.yydebug = (org.truffleruby.parser.parser.YYDebug) ayydebug;
    return yyparse(yyLex);
  }

  /** initial size and increment of the state/value stack [default 256].
      This is not final so that it can be overwritten outside of invocations
      of {@link #yyparse}.
    */
  protected int yyMax;

  /** executed at the beginning of a reduce action.
      Used as <tt>$$ = yyDefault($1)</tt>, prior to the user-specified action, if any.
      Can be overwritten to provide deep copy, etc.
      @param first value for <tt>$1</tt>, or <tt>null</tt>.
      @return first.
    */
  protected Object yyDefault (Object first) {
    return first;
  }

  /** the generated parser.
      Maintains a dynamic state and value stack.
      @param yyLex scanner.
      @return result of the last reduction, if any.
    */
  public Object yyparse (RubyLexer yyLex) {
    if (yyMax <= 0) yyMax = 256;			// initial size
    int yyState = 0, yyStates[] = new int[yyMax];	// state stack
    Object yyVal = null, yyVals[] = new Object[yyMax];	// value stack
    int yyToken = -1;					// current input
    int yyErrorFlag = 0;				// #tokens to shift

    yyLoop: for (int yyTop = 0;; ++ yyTop) {
      if (yyTop >= yyStates.length) {			// dynamically increase
        int[] i = new int[yyStates.length+yyMax];
        System.arraycopy(yyStates, 0, i, 0, yyStates.length);
        yyStates = i;
        Object[] o = new Object[yyVals.length+yyMax];
        System.arraycopy(yyVals, 0, o, 0, yyVals.length);
        yyVals = o;
      }
      yyStates[yyTop] = yyState;
      yyVals[yyTop] = yyVal;
      if (yydebug != null) yydebug.push(yyState, yyVal);

      yyDiscarded: for (;;) {	// discarding a token does not change stack
        int yyN;
        if ((yyN = yyDefRed[yyState]) == 0) {	// else [default] reduce (yyN)
          if (yyToken < 0) {
//            yyToken = yyLex.advance() ? yyLex.token() : 0;
            yyToken = yyLex.nextToken();
            if (yydebug != null)
              yydebug.lex(yyState, yyToken, yyName(yyToken), yyLex.value());
          }
          if ((yyN = yySindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < yyTable.length && yyCheck[yyN] == yyToken) {
            if (yydebug != null)
              yydebug.shift(yyState, yyTable[yyN], yyErrorFlag-1);
            yyState = yyTable[yyN];		// shift to yyN
            yyVal = yyLex.value();
            yyToken = -1;
            if (yyErrorFlag > 0) -- yyErrorFlag;
            continue yyLoop;
          }
          if ((yyN = yyRindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < yyTable.length && yyCheck[yyN] == yyToken)
            yyN = yyTable[yyN];			// reduce (yyN)
          else
            switch (yyErrorFlag) {
  
            case 0:
              support.yyerror("syntax error", yyExpecting(yyState), yyNames[yyToken]);
              if (yydebug != null) yydebug.error("syntax error");
              break;
  
            case 1: case 2:
              yyErrorFlag = 3;
              do {
                if ((yyN = yySindex[yyStates[yyTop]]) != 0
                    && (yyN += yyErrorCode) >= 0 && yyN < yyTable.length
                    && yyCheck[yyN] == yyErrorCode) {
                  if (yydebug != null)
                    yydebug.shift(yyStates[yyTop], yyTable[yyN], 3);
                  yyState = yyTable[yyN];
                  yyVal = yyLex.value();
                  continue yyLoop;
                }
                if (yydebug != null) yydebug.pop(yyStates[yyTop]);
              } while (-- yyTop >= 0);
              if (yydebug != null) yydebug.reject();
              support.yyerror("irrecoverable syntax error");
              break;

            case 3:
              if (yyToken == 0) {
                if (yydebug != null) yydebug.reject();
                support.yyerror("irrecoverable syntax error at end-of-file");
              }
              if (yydebug != null)
                yydebug.discard(yyState, yyToken, yyName(yyToken),
  							yyLex.value());
              yyToken = -1;
              continue yyDiscarded;		// leave stack alone
            }
        }
        int yyV = yyTop + 1-yyLen[yyN];
        if (yydebug != null)
          yydebug.reduce(yyState, yyStates[yyV-1], yyN, yyRule[yyN], yyLen[yyN]);
        ParserState state = states[yyN];
        if (state == null) {
            yyVal = yyDefault(yyV > yyTop ? null : yyVals[yyV]);
        } else {
            yyVal = state.execute(support, lexer, yyVal, yyVals, yyTop);
        }
//        switch (yyN) {
// ACTIONS_END
//        }
        yyTop -= yyLen[yyN];
        yyState = yyStates[yyTop];
        int yyM = yyLhs[yyN];
        if (yyState == 0 && yyM == 0) {
          if (yydebug != null) yydebug.shift(0, yyFinal);
          yyState = yyFinal;
          if (yyToken < 0) {
            yyToken = yyLex.nextToken();
//            yyToken = yyLex.advance() ? yyLex.token() : 0;
            if (yydebug != null)
               yydebug.lex(yyState, yyToken,yyName(yyToken), yyLex.value());
          }
          if (yyToken == 0) {
            if (yydebug != null) yydebug.accept(yyVal);
            return yyVal;
          }
          continue yyLoop;
        }
        if ((yyN = yyGindex[yyM]) != 0 && (yyN += yyState) >= 0
            && yyN < yyTable.length && yyCheck[yyN] == yyState)
          yyState = yyTable[yyN];
        else
          yyState = yyDgoto[yyM];
        if (yydebug != null) yydebug.shift(yyStates[yyTop], yyState);
        continue yyLoop;
      }
    }
  }

static ParserState[] states = new ParserState[691];
static {
states[1] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_BEG);
    support.initTopLocalVariables();
    return yyVal;
};
states[2] = (support, lexer, yyVal, yyVals, yyTop) -> {
  /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
                  if (((ParseNode)yyVals[0+yyTop]) != null) {
                      /* last expression should not be void */
                      if (((ParseNode)yyVals[0+yyTop]) instanceof BlockParseNode) {
                          support.checkUselessStatement(((BlockParseNode)yyVals[0+yyTop]).getLast());
                      } else {
                          support.checkUselessStatement(((ParseNode)yyVals[0+yyTop]));
                      }
                  }
                  support.getResult().setAST(support.addRootNode(((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[3] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-1+yyTop]) instanceof BlockParseNode) {
        support.checkUselessStatements(((BlockParseNode)yyVals[-1+yyTop]));
    }
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[5] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newline_node(((ParseNode)yyVals[0+yyTop]), support.getPosition(((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[6] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.appendToBlock(((ParseNode)yyVals[-2+yyTop]), support.newline_node(((ParseNode)yyVals[0+yyTop]), support.getPosition(((ParseNode)yyVals[0+yyTop]))));
    return yyVal;
};
states[7] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[9] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.getResult().addBeginNode(new PreExe19ParseNode(((SourceIndexLength)yyVals[-3+yyTop]), support.getCurrentScope(), ((ParseNode)yyVals[-1+yyTop])));
    yyVal = null;
    return yyVal;
};
states[10] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode node = ((ParseNode)yyVals[-3+yyTop]);

    if (((RescueBodyParseNode)yyVals[-2+yyTop]) != null) {
        node = new RescueParseNode(support.getPosition(((ParseNode)yyVals[-3+yyTop])), ((ParseNode)yyVals[-3+yyTop]), ((RescueBodyParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    } else if (((ParseNode)yyVals[-1+yyTop]) != null) {
        support.warn(support.getPosition(((ParseNode)yyVals[-3+yyTop])), "else without rescue is useless");
        node = support.appendToBlock(((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    }
    if (((ParseNode)yyVals[0+yyTop]) != null) {
        if (node != null) {
            node = new EnsureParseNode(support.extendedUntil(support.getPosition(((ParseNode)yyVals[-3+yyTop])), support.getPosition(((ParseNode)yyVals[0+yyTop]))), support.makeNullNil(node), ((ParseNode)yyVals[0+yyTop]));
        } else {
            node = support.appendToBlock(((ParseNode)yyVals[0+yyTop]), NilImplicitParseNode.NIL);
        }
    }

    yyVal = node;
    return yyVal;
};
states[11] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-1+yyTop]) instanceof BlockParseNode) {
        support.checkUselessStatements(((BlockParseNode)yyVals[-1+yyTop]));
    }
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[13] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newline_node(((ParseNode)yyVals[0+yyTop]), support.getPosition(((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[14] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.appendToBlock(((ParseNode)yyVals[-2+yyTop]), support.newline_node(((ParseNode)yyVals[0+yyTop]), support.getPosition(((ParseNode)yyVals[0+yyTop]))));
    return yyVal;
};
states[15] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[16] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[17] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("BEGIN is permitted only at toplevel");
    return yyVal;
};
states[18] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BeginParseNode(((SourceIndexLength)yyVals[-4+yyTop]), support.makeNullNil(((ParseNode)yyVals[-3+yyTop])));
    return yyVal;
};
states[19] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_FNAME|EXPR_FITEM);
    return yyVal;
};
states[20] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newAlias(((SourceIndexLength)yyVals[-3+yyTop]), ((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[21] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new VAliasParseNode(((SourceIndexLength)yyVals[-2+yyTop]), support.symbolID(((TruffleString)yyVals[-1+yyTop])), support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[22] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new VAliasParseNode(((SourceIndexLength)yyVals[-2+yyTop]), support.symbolID(((TruffleString)yyVals[-1+yyTop])), support.symbolID(((BackRefParseNode)yyVals[0+yyTop]).getByteName()));
    return yyVal;
};
states[23] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("can't make alias for the number variables");
    return yyVal;
};
states[24] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[25] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IfParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[-2+yyTop]), null);
    ((ParseNode)yyVal).extendPosition(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[26] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IfParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.getConditionNode(((ParseNode)yyVals[0+yyTop])), null, ((ParseNode)yyVals[-2+yyTop]));
    ((ParseNode)yyVal).extendPosition(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[27] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-2+yyTop]) != null && ((ParseNode)yyVals[-2+yyTop]) instanceof BeginParseNode) {
        yyVal = new WhileParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((BeginParseNode)yyVals[-2+yyTop]).getBodyNode(), false);
    } else {
        yyVal = new WhileParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[-2+yyTop]), true);
    }
    return yyVal;
};
states[28] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-2+yyTop]) != null && ((ParseNode)yyVals[-2+yyTop]) instanceof BeginParseNode) {
        yyVal = new UntilParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((BeginParseNode)yyVals[-2+yyTop]).getBodyNode(), false);
    } else {
        yyVal = new UntilParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[-2+yyTop]), true);
    }
    return yyVal;
};
states[29] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newRescueModNode(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[30] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) {
        support.warn(((SourceIndexLength)yyVals[-3+yyTop]), "END in method; use at_exit");
    }
    yyVal = new PostExeParseNode(((SourceIndexLength)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[32] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    ((MultipleAsgnParseNode)yyVals[-2+yyTop]).setValueNode(((ParseNode)yyVals[0+yyTop]));
    yyVal = ((MultipleAsgnParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[33] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.node_assign(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[34] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[-2+yyTop]));
    yyVal = support.node_assign(((MultipleAsgnParseNode)yyVals[-4+yyTop]), support.newRescueModNode(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[35] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(((ParseNode)yyVals[0+yyTop]));
    yyVal = ((MultipleAsgnParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[37] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.node_assign(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[38] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));

    SourceIndexLength pos = ((AssignableParseNode)yyVals[-2+yyTop]).getPosition();
    TruffleString asgnOp = ((TruffleString)yyVals[-1+yyTop]);
    if (asgnOp == TStringConstants.OR_OR) {
        ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(((ParseNode)yyVals[0+yyTop]));
        yyVal = new OpAsgnOrParseNode(pos, support.gettable2(((AssignableParseNode)yyVals[-2+yyTop])), ((AssignableParseNode)yyVals[-2+yyTop]));
    } else if (asgnOp == TStringConstants.AMPERSAND_AMPERSAND) {
        ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(((ParseNode)yyVals[0+yyTop]));
        yyVal = new OpAsgnAndParseNode(pos, support.gettable2(((AssignableParseNode)yyVals[-2+yyTop])), ((AssignableParseNode)yyVals[-2+yyTop]));
    } else {
        ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableParseNode)yyVals[-2+yyTop])), asgnOp, ((ParseNode)yyVals[0+yyTop])));
        yyVal = ((AssignableParseNode)yyVals[-2+yyTop]);
    }
    return yyVal;
};
states[39] = (support, lexer, yyVal, yyVals, yyTop) -> {
  /* FIXME: arg_concat logic missing for opt_call_args*/
                    yyVal = support.new_opElementAsgnNode(((ParseNode)yyVals[-5+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[40] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newOpAsgn(support.getPosition(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[41] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newOpAsgn(support.getPosition(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[42] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength pos = ((ParseNode)yyVals[-4+yyTop]).getPosition();
    yyVal = support.newOpConstAsgn(pos, support.new_colon2(pos, ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-2+yyTop])), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[43] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newOpAsgn(support.getPosition(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[44] = (support, lexer, yyVal, yyVals, yyTop) -> {
    MethodStubHolder stub = ((MethodStubHolder)yyVals[-3+yyTop]);
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[0+yyTop]));
    support.endless_method_name(stub.name());
    support.restore_defun(lexer, stub);

                   yyVal = new DefnParseNode(support.extendedUntil(support.getPosition(((ParseNode)yyVals[-3+yyTop])), support.getPosition(((ParseNode)yyVals[0+yyTop]))), support.symbolID(stub.name()), (ArgsParseNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
	/*endless_method_name(p, $<node>1, &@1);*/
	/*restore_defun(p, $<node>1->nd_defn);*/
    return yyVal;
};
states[45] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.backrefAssignError(((ParseNode)yyVals[-2+yyTop]));
    return yyVal;
};
states[46] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[47] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[-2+yyTop]));
    yyVal = support.newRescueModNode(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[50] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newAndNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), ((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[51] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newOrNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), ((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[52] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(support.getConditionNode(((ParseNode)yyVals[0+yyTop])), TStringConstants.BANG);
    return yyVal;
};
states[53] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[55] = (support, lexer, yyVal, yyVals, yyTop) -> {
    TruffleString fname = support.symbolID(((TruffleString)yyVals[0+yyTop]));
		TruffleString currentArg = lexer.getCurrentArg();
		support.assignableInCurr(fname, NilImplicitParseNode.NIL);
		support.pushLocalScope();
		lexer.setCurrentArg(null);
		support.setInDef(true);
		yyVal = new MethodStubHolder(fname, currentArg);


		/*
    ID fname = get_id($1);
    ID cur_arg = p->cur_arg;
    YYSTYPE c = {.ctxt = p->ctxt};
    numparam_name(p, fname);
    local_push(p, 0);
    p->cur_arg = 0;
    p->ctxt.in_def = 1;
    */
    /* $<node>$ = NEW_NODE(NODE_SELF, /*vid* /cur_arg, /*mid* /fname, /*cval* /c.val, &@$);*/
    return yyVal;
};
states[56] = (support, lexer, yyVal, yyVals, yyTop) -> {
    /* KEVIN MARKER*/
			yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[57] = (support, lexer, yyVal, yyVals, yyTop) -> {
   	lexer.setState(EXPR_FNAME);
   	support.setInDef(true);

   	/*
SET_LEX_STATE(EXPR_FNAME);
p->ctxt.in_argdef = 1;
*/
    return yyVal;
};
states[58] = (support, lexer, yyVal, yyVals, yyTop) -> {
   	lexer.setState(EXPR_ENDFN|EXPR_LABEL);
   	yyVal = ((ParseNode)yyVals[0+yyTop]);


/* SET_LEX_STATE(EXPR_ENDFN|EXPR_LABEL); /* force for args * /*/
/* $$ = $5;*/

    return yyVal;
};
states[59] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[63] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[64] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((IterParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[65] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_fcall(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[66] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.frobnicate_fcall_args(((FCallParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    yyVal = ((FCallParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[67] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.frobnicate_fcall_args(((FCallParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((IterParseNode)yyVals[0+yyTop]));
    yyVal = ((FCallParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[68] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[69] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((IterParseNode)yyVals[0+yyTop])); 
    return yyVal;
};
states[70] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[71] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((IterParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[72] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_super(((SourceIndexLength)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[73] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_yield(((SourceIndexLength)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[74] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ReturnParseNode(((SourceIndexLength)yyVals[-1+yyTop]), support.ret_args(((ParseNode)yyVals[0+yyTop]), ((SourceIndexLength)yyVals[-1+yyTop])));
    return yyVal;
};
states[75] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BreakParseNode(((SourceIndexLength)yyVals[-1+yyTop]), support.ret_args(((ParseNode)yyVals[0+yyTop]), ((SourceIndexLength)yyVals[-1+yyTop])));
    return yyVal;
};
states[76] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new NextParseNode(((SourceIndexLength)yyVals[-1+yyTop]), support.ret_args(((ParseNode)yyVals[0+yyTop]), ((SourceIndexLength)yyVals[-1+yyTop])));
    return yyVal;
};
states[78] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[79] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((MultipleAsgnParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[80] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((SourceIndexLength)yyVals[-2+yyTop]), support.newArrayNode(((SourceIndexLength)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop])), null, null);
    return yyVal;
};
states[81] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[0+yyTop]).getPosition(), ((ListParseNode)yyVals[0+yyTop]), null, null);
    return yyVal;
};
states[82] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]).add(((ParseNode)yyVals[0+yyTop])), null, null);
    return yyVal;
};
states[83] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-2+yyTop]).getPosition(), ((ListParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]), (ListParseNode) null);
    return yyVal;
};
states[84] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-4+yyTop]).getPosition(), ((ListParseNode)yyVals[-4+yyTop]), ((ParseNode)yyVals[-2+yyTop]), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[85] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]), new StarParseNode(lexer.getPosition()), null);
    return yyVal;
};
states[86] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), new StarParseNode(lexer.getPosition()), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[87] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ParseNode)yyVals[0+yyTop]).getPosition(), null, ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[88] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ParseNode)yyVals[-2+yyTop]).getPosition(), null, ((ParseNode)yyVals[-2+yyTop]), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[89] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(lexer.getPosition(), null, new StarParseNode(lexer.getPosition()), null);
    return yyVal;
};
states[90] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(lexer.getPosition(), null, new StarParseNode(lexer.getPosition()), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[92] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[93] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newArrayNode(((ParseNode)yyVals[-1+yyTop]).getPosition(), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[94] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[95] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newArrayNode(((ParseNode)yyVals[0+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[96] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[97] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.assignableLabelOrIdentifier(((TruffleString)yyVals[0+yyTop]), null);
    return yyVal;
};
states[98] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new InstAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[99] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new GlobalAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[100] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) support.compile_error("dynamic constant assignment");
    yyVal = new ConstDeclParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), null, NilImplicitParseNode.NIL);
    return yyVal;
};
states[101] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ClassVarAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[102] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to nil");
    yyVal = null;
    return yyVal;
};
states[103] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't change the value of self");
    yyVal = null;
    return yyVal;
};
states[104] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to true");
    yyVal = null;
    return yyVal;
};
states[105] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to false");
    yyVal = null;
    return yyVal;
};
states[106] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __FILE__");
    yyVal = null;
    return yyVal;
};
states[107] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __LINE__");
    yyVal = null;
    return yyVal;
};
states[108] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __ENCODING__");
    yyVal = null;
    return yyVal;
};
states[109] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.aryset(((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[110] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.attrset(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[111] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.attrset(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[112] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.attrset(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[113] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) support.yyerror("dynamic constant assignment");

    SourceIndexLength position = support.getPosition(((ParseNode)yyVals[-2+yyTop]));

    yyVal = new ConstDeclParseNode(position, (TruffleString) null, support.new_colon2(position, ((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[114] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) {
        support.yyerror("dynamic constant assignment");
    }

    SourceIndexLength position = lexer.tokline;

    yyVal = new ConstDeclParseNode(position, (TruffleString) null, support.new_colon3(position, ((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[115] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.backrefAssignError(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[116] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.assignableLabelOrIdentifier(((TruffleString)yyVals[0+yyTop]), null);
    return yyVal;
};
states[117] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new InstAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[118] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new GlobalAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[119] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) support.compile_error("dynamic constant assignment");

    yyVal = new ConstDeclParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), null, NilImplicitParseNode.NIL);
    return yyVal;
};
states[120] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ClassVarAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[121] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to nil");
    yyVal = null;
    return yyVal;
};
states[122] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't change the value of self");
    yyVal = null;
    return yyVal;
};
states[123] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to true");
    yyVal = null;
    return yyVal;
};
states[124] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to false");
    yyVal = null;
    return yyVal;
};
states[125] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __FILE__");
    yyVal = null;
    return yyVal;
};
states[126] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __LINE__");
    yyVal = null;
    return yyVal;
};
states[127] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __ENCODING__");
    yyVal = null;
    return yyVal;
};
states[128] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.aryset(((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[129] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.attrset(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[130] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.attrset(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[131] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.attrset(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[132] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) {
        support.yyerror("dynamic constant assignment");
    }

    SourceIndexLength position = support.getPosition(((ParseNode)yyVals[-2+yyTop]));

    yyVal = new ConstDeclParseNode(position, (TruffleString) null, support.new_colon2(position, ((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[133] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) {
        support.yyerror("dynamic constant assignment");
    }

    SourceIndexLength position = lexer.tokline;

    yyVal = new ConstDeclParseNode(position, (TruffleString) null, support.new_colon3(position, ((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[134] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.backrefAssignError(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[135] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("class/module name must be CONSTANT");
    return yyVal;
};
states[136] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[137] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_colon3(lexer.tokline, ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[138] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_colon2(lexer.tokline, null, ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[139] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_colon2(support.getPosition(((ParseNode)yyVals[-2+yyTop])), ((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[140] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[141] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[142] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[143] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_ENDFN);
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[144] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_ENDFN);
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[145] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new LiteralParseNode(lexer.getPosition(), support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[146] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new LiteralParseNode(lexer.getPosition(), support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[147] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((LiteralParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[148] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[149] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newUndef(((ParseNode)yyVals[0+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[150] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_FNAME|EXPR_FITEM);
    return yyVal;
};
states[151] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.appendToBlock(((ParseNode)yyVals[-3+yyTop]), support.newUndef(((ParseNode)yyVals[-3+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[152] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[153] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[154] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[155] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[156] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[157] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[158] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[159] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[160] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[161] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[162] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[163] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[164] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[165] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[166] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[167] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[168] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[169] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[170] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[171] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[172] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[173] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[174] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[175] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[176] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[177] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[178] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[179] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[180] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[181] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[182] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.__LINE__.bytes;
    return yyVal;
};
states[183] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.__FILE__.bytes;
    return yyVal;
};
states[184] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.__ENCODING__.bytes;
    return yyVal;
};
states[185] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.LBEGIN.bytes;
    return yyVal;
};
states[186] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.LEND.bytes;
    return yyVal;
};
states[187] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.ALIAS.bytes;
    return yyVal;
};
states[188] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.AND.bytes;
    return yyVal;
};
states[189] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.BEGIN.bytes;
    return yyVal;
};
states[190] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.BREAK.bytes;
    return yyVal;
};
states[191] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.CASE.bytes;
    return yyVal;
};
states[192] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.CLASS.bytes;
    return yyVal;
};
states[193] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.DEF.bytes;
    return yyVal;
};
states[194] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.DEFINED_P.bytes;
    return yyVal;
};
states[195] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.DO.bytes;
    return yyVal;
};
states[196] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.ELSE.bytes;
    return yyVal;
};
states[197] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.ELSIF.bytes;
    return yyVal;
};
states[198] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.END.bytes;
    return yyVal;
};
states[199] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.ENSURE.bytes;
    return yyVal;
};
states[200] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.FALSE.bytes;
    return yyVal;
};
states[201] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.FOR.bytes;
    return yyVal;
};
states[202] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.IN.bytes;
    return yyVal;
};
states[203] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.MODULE.bytes;
    return yyVal;
};
states[204] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.NEXT.bytes;
    return yyVal;
};
states[205] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.NIL.bytes;
    return yyVal;
};
states[206] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.NOT.bytes;
    return yyVal;
};
states[207] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.OR.bytes;
    return yyVal;
};
states[208] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.REDO.bytes;
    return yyVal;
};
states[209] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.RESCUE.bytes;
    return yyVal;
};
states[210] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.RETRY.bytes;
    return yyVal;
};
states[211] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.RETURN.bytes;
    return yyVal;
};
states[212] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.SELF.bytes;
    return yyVal;
};
states[213] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.SUPER.bytes;
    return yyVal;
};
states[214] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.THEN.bytes;
    return yyVal;
};
states[215] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.TRUE.bytes;
    return yyVal;
};
states[216] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.UNDEF.bytes;
    return yyVal;
};
states[217] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.WHEN.bytes;
    return yyVal;
};
states[218] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.YIELD.bytes;
    return yyVal;
};
states[219] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.IF.bytes;
    return yyVal;
};
states[220] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.UNLESS.bytes;
    return yyVal;
};
states[221] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.WHILE.bytes;
    return yyVal;
};
states[222] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.UNTIL.bytes;
    return yyVal;
};
states[223] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = RubyLexer.Keyword.RESCUE.bytes;
    return yyVal;
};
states[224] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.node_assign(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    /* FIXME: Consider fixing node_assign itself rather than single case*/
    ((ParseNode)yyVal).extendPosition(((ParseNode)yyVals[-2+yyTop]));
    return yyVal;
};
states[225] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));

    SourceIndexLength pos = ((AssignableParseNode)yyVals[-2+yyTop]).getPosition();
    TruffleString asgnOp = ((TruffleString)yyVals[-1+yyTop]);
    if (asgnOp == TStringConstants.OR_OR) {
        ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(((ParseNode)yyVals[0+yyTop]));
        yyVal = new OpAsgnOrParseNode(pos, support.gettable2(((AssignableParseNode)yyVals[-2+yyTop])), ((AssignableParseNode)yyVals[-2+yyTop]));
    } else if (asgnOp == TStringConstants.AMPERSAND_AMPERSAND) {
        ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(((ParseNode)yyVals[0+yyTop]));
        yyVal = new OpAsgnAndParseNode(pos, support.gettable2(((AssignableParseNode)yyVals[-2+yyTop])), ((AssignableParseNode)yyVals[-2+yyTop]));
    } else {
        ((AssignableParseNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableParseNode)yyVals[-2+yyTop])), asgnOp, ((ParseNode)yyVals[0+yyTop])));
        yyVal = ((AssignableParseNode)yyVals[-2+yyTop]);
    }
    return yyVal;
};
states[226] = (support, lexer, yyVal, yyVals, yyTop) -> {
  /* FIXME: arg_concat missing for opt_call_args*/
                    yyVal = support.new_opElementAsgnNode(((ParseNode)yyVals[-5+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[227] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newOpAsgn(support.getPosition(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[228] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newOpAsgn(support.getPosition(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[229] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newOpAsgn(support.getPosition(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[230] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength pos = support.getPosition(((ParseNode)yyVals[-4+yyTop]));
    yyVal = support.newOpConstAsgn(pos, support.new_colon2(pos, ((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-2+yyTop])), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[231] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength pos = lexer.getPosition();
    yyVal = support.newOpConstAsgn(pos, new Colon3ParseNode(pos, support.symbolID(((TruffleString)yyVals[-2+yyTop]))), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[232] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.backrefAssignError(((ParseNode)yyVals[-2+yyTop]));
    return yyVal;
};
states[233] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[-2+yyTop]));
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    
    boolean isLiteral = ((ParseNode)yyVals[-2+yyTop]) instanceof FixnumParseNode && ((ParseNode)yyVals[0+yyTop]) instanceof FixnumParseNode;
    yyVal = new DotParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.makeNullNil(((ParseNode)yyVals[-2+yyTop])), support.makeNullNil(((ParseNode)yyVals[0+yyTop])), false, isLiteral);
    return yyVal;
};
states[234] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.checkExpression(((ParseNode)yyVals[-1+yyTop]));

    boolean isLiteral = ((ParseNode)yyVals[-1+yyTop]) instanceof FixnumParseNode;
    yyVal = new DotParseNode(support.getPosition(((ParseNode)yyVals[-1+yyTop])), support.makeNullNil(((ParseNode)yyVals[-1+yyTop])), NilImplicitParseNode.NIL, false, isLiteral);
    return yyVal;
};
states[235] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));

    boolean isLiteral = ((ParseNode)yyVals[0+yyTop]) instanceof FixnumParseNode;
    yyVal = new DotParseNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), NilImplicitParseNode.NIL, support.makeNullNil(((ParseNode)yyVals[0+yyTop])), false, isLiteral);
    return yyVal;
};
states[236] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[-2+yyTop]));
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));

    boolean isLiteral = ((ParseNode)yyVals[-2+yyTop]) instanceof FixnumParseNode && ((ParseNode)yyVals[0+yyTop]) instanceof FixnumParseNode;
    yyVal = new DotParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), support.makeNullNil(((ParseNode)yyVals[-2+yyTop])), support.makeNullNil(((ParseNode)yyVals[0+yyTop])), true, isLiteral);
    return yyVal;
};
states[237] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.checkExpression(((ParseNode)yyVals[-1+yyTop]));

    boolean isLiteral = ((ParseNode)yyVals[-1+yyTop]) instanceof FixnumParseNode;
    yyVal = new DotParseNode(support.getPosition(((ParseNode)yyVals[-1+yyTop])), support.makeNullNil(((ParseNode)yyVals[-1+yyTop])), NilImplicitParseNode.NIL, true, isLiteral);
    return yyVal;
};
states[238] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));

    boolean isLiteral = ((ParseNode)yyVals[0+yyTop]) instanceof FixnumParseNode;
    yyVal = new DotParseNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), NilImplicitParseNode.NIL, support.makeNullNil(((ParseNode)yyVals[0+yyTop])), true, isLiteral);
    return yyVal;
};
states[239] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[240] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[241] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[242] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[243] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[244] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[245] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((NumericParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition()), ((TruffleString)yyVals[-3+yyTop]));
    return yyVal;
};
states[246] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[247] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[248] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[249] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[250] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[251] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[252] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[253] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[254] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[255] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[256] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getMatchNode(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
  /* ENEBO
        $$ = match_op($1, $3);
        if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
            $$ = reg_named_capture_assign($1->nd_lit, $$);
        }
  */
    return yyVal;
};
states[257] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[258] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(support.getConditionNode(((ParseNode)yyVals[0+yyTop])), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[259] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[0+yyTop]), ((TruffleString)yyVals[-1+yyTop]));
    return yyVal;
};
states[260] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[261] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[262] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newAndNode(((ParseNode)yyVals[-2+yyTop]).getPosition(), ((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[263] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newOrNode(((ParseNode)yyVals[-2+yyTop]).getPosition(), ((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[264] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_defined(((SourceIndexLength)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[265] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[-5+yyTop]));
    yyVal = new IfParseNode(support.getPosition(((ParseNode)yyVals[-5+yyTop])), support.getConditionNode(((ParseNode)yyVals[-5+yyTop])), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[266] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* endless_method_name(p, $<node>1, &@1);*/
			/* restore_defun(p, $<node>1->nd_defn);*/
		    /*%%%*/
			/* $$ = set_defun_body(p, $1, $2, $4, &@$);*/
		    /*% %*/
    return yyVal;
};
states[267] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* endless_method_name(p, $<node>1, &@1);*/
			/* restore_defun(p, $<node>1->nd_defn);*/
		    /*%%%*/
			/* $4 = rescued_expr(p, $4, $6, &@4, &@5, &@6);*/
			/* $$ = set_defun_body(p, $1, $2, $4, &@$);*/
		    /*% %*/
    return yyVal;
};
states[268] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* endless_method_name(p, $<node>1, &@1);*/
			/* restore_defun(p, $<node>1->nd_defn);*/
		    /*%%%*/
			/* $$ = set_defun_body(p, $1, $2, $4, &@$);*/
		    /*%
// $1 = get_value($1);
		    %*/
    return yyVal;
};
states[269] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* endless_method_name(p, $<node>1, &@1);*/
			/* restore_defun(p, $<node>1->nd_defn);*/
		    /*%%%*/
			/* $4 = rescued_expr(p, $4, $6, &@4, &@5, &@6);*/
			/* $$ = set_defun_body(p, $1, $2, $4, &@$);*/
		    /*%
// $1 = get_value($1);
		    %*/
    return yyVal;
};
states[270] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[271] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[272] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[273] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[274] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[275] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[276] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.warning(lexer.getPosition(), "comparison '" + ((TruffleString)yyVals[-1+yyTop]).toJavaStringUncached() + "' after comparison");
    yyVal = support.getOperatorCallNode(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), lexer.getPosition());
    return yyVal;
};
states[277] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.makeNullNil(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[279] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[280] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((HashParseNode)yyVals[-1+yyTop]).setKeywordArguments(true);
    yyVal = support.arg_append(((ParseNode)yyVals[-3+yyTop]), support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[281] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((HashParseNode)yyVals[-1+yyTop]).setKeywordArguments(true);
    yyVal = support.newArrayNode(((HashParseNode)yyVals[-1+yyTop]).getPosition(), support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[282] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[283] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[-2+yyTop]));
    yyVal = support.newRescueModNode(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[284] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    if (yyVal != null) ((ParseNode)yyVal).extendPosition(((SourceIndexLength)yyVals[-2+yyTop]));
    return yyVal;
};
states[285] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength position = support.getPosition(null);
    /* NOTE(norswap, 02 Jun 2021): location (0) arg is unused*/
    SplatParseNode splat = support.newSplatNode(position, new LocalVarParseNode(position, 0, ParserSupport.FORWARD_ARGS_REST_VAR));
    HashParseNode kwrest = new HashParseNode(position, support.createKeyValue(null, new LocalVarParseNode(position, 0, ParserSupport.FORWARD_ARGS_KWREST_VAR)));
    kwrest.setKeywordArguments(true);
    BlockPassParseNode block = new BlockPassParseNode(position, new LocalVarParseNode(position, 0, ParserSupport.FORWARD_ARGS_BLOCK_VAR));
    yyVal = support.arg_concat(support.getPosition(((ParseNode)yyVals[-3+yyTop])), ((ParseNode)yyVals[-3+yyTop]), splat);
    yyVal = support.arg_append((ParseNode) yyVal, kwrest);
    yyVal = support.arg_blk_pass((ParseNode) yyVal, block);
    return yyVal;
};
states[286] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength position = support.getPosition(null);
    /* NOTE(norswap, 06 Nov 2020): location (0) arg is unused*/
    SplatParseNode splat = support.newSplatNode(position, new LocalVarParseNode(position, 0, ParserSupport.FORWARD_ARGS_REST_VAR));
    HashParseNode kwrest = new HashParseNode(position, support.createKeyValue(null, new LocalVarParseNode(position, 0, ParserSupport.FORWARD_ARGS_KWREST_VAR)));
    kwrest.setKeywordArguments(true);
    BlockPassParseNode block = new BlockPassParseNode(position, new LocalVarParseNode(position, 0, ParserSupport.FORWARD_ARGS_BLOCK_VAR));
    yyVal = support.arg_append(splat, kwrest);
    yyVal = support.arg_blk_pass((ParseNode) yyVal, block);
    return yyVal;
};
states[291] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[292] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((HashParseNode)yyVals[-1+yyTop]).setKeywordArguments(true);
    yyVal = support.arg_append(((ParseNode)yyVals[-3+yyTop]), support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[293] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((HashParseNode)yyVals[-1+yyTop]).setKeywordArguments(true);
    yyVal = support.newArrayNode(((HashParseNode)yyVals[-1+yyTop]).getPosition(), support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[294] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = support.newArrayNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[295] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.arg_blk_pass(((ParseNode)yyVals[-1+yyTop]), ((BlockPassParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[296] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((HashParseNode)yyVals[-1+yyTop]).setKeywordArguments(true);
    yyVal = support.newArrayNode(((HashParseNode)yyVals[-1+yyTop]).getPosition(), support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop])));
    yyVal = support.arg_blk_pass((ParseNode)yyVal, ((BlockPassParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[297] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((HashParseNode)yyVals[-1+yyTop]).setKeywordArguments(true);
    yyVal = support.arg_append(((ParseNode)yyVals[-3+yyTop]), support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop])));
    yyVal = support.arg_blk_pass((ParseNode)yyVal, ((BlockPassParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[298] = (support, lexer, yyVal, yyVals, yyTop) -> yyVal;
states[299] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getCmdArgumentState().getStack();
    lexer.getCmdArgumentState().begin();
    return yyVal;
};
states[300] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[301] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BlockPassParseNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[302] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (!support.local_id(ParserSupport.FORWARD_ARGS_BLOCK_VAR)) {
        support.yyerror("no anonymous block parameter");
    }

    yyVal = new BlockPassParseNode(lexer.tokline, new LocalVarParseNode(support.getPosition(null), 0, ParserSupport.FORWARD_ARGS_BLOCK_VAR));
    return yyVal;
};
states[303] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((BlockPassParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[305] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newArrayNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[306] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newSplatNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[307] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode node = support.splat_array(((ParseNode)yyVals[-2+yyTop]));

    if (node != null) {
        yyVal = support.list_append(node, ((ParseNode)yyVals[0+yyTop]));
    } else {
        yyVal = support.arg_append(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    }
    return yyVal;
};
states[308] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode node = null;

    /* FIXME: lose syntactical elements here (and others like this)*/
    if (((ParseNode)yyVals[0+yyTop]) instanceof ArrayParseNode &&
        (node = support.splat_array(((ParseNode)yyVals[-3+yyTop]))) != null) {
        yyVal = support.list_concat(node, ((ParseNode)yyVals[0+yyTop]));
    } else {
        yyVal = support.arg_concat(support.getPosition(((ParseNode)yyVals[-3+yyTop])), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    }
    return yyVal;
};
states[309] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[310] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[311] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode node = support.splat_array(((ParseNode)yyVals[-2+yyTop]));

    if (node != null) {
        yyVal = support.list_append(node, ((ParseNode)yyVals[0+yyTop]));
    } else {
        yyVal = support.arg_append(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    }
    return yyVal;
};
states[312] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode node = null;

    if (((ParseNode)yyVals[0+yyTop]) instanceof ArrayParseNode &&
        (node = support.splat_array(((ParseNode)yyVals[-3+yyTop]))) != null) {
        yyVal = support.list_concat(node, ((ParseNode)yyVals[0+yyTop]));
    } else {
        yyVal = support.arg_concat(((ParseNode)yyVals[-3+yyTop]).getPosition(), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    }
    return yyVal;
};
states[313] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newSplatNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[320] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
    return yyVal;
};
states[321] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
    return yyVal;
};
states[324] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_fcall(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[325] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getCmdArgumentState().getStack();
    lexer.getCmdArgumentState().reset();
    return yyVal;
};
states[326] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getCmdArgumentState().reset(((Long)yyVals[-2+yyTop]).longValue());
    yyVal = new BeginParseNode(((SourceIndexLength)yyVals[-3+yyTop]), support.makeNullNil(((ParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[327] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_ENDARG);
    return yyVal;
};
states[328] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null; /*FIXME: Should be implicit nil?*/
    return yyVal;
};
states[329] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getCmdArgumentState().getStack();
    lexer.getCmdArgumentState().reset();
    return yyVal;
};
states[330] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_ENDARG); 
    return yyVal;
};
states[331] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getCmdArgumentState().reset(((Long)yyVals[-3+yyTop]).longValue());
    yyVal = ((ParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[332] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-1+yyTop]) != null) {
        /* compstmt position includes both parens around it*/
        ((ParseNode)yyVals[-1+yyTop]).extendPosition(((SourceIndexLength)yyVals[-2+yyTop]));
        yyVal = ((ParseNode)yyVals[-1+yyTop]);
    } else {
        yyVal = new NilParseNode(((SourceIndexLength)yyVals[-2+yyTop]));
    }
    return yyVal;
};
states[333] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_colon2(support.getPosition(((ParseNode)yyVals[-2+yyTop])), ((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[334] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_colon3(lexer.tokline, ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[335] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength position = support.getPosition(((ParseNode)yyVals[-1+yyTop]));
    if (((ParseNode)yyVals[-1+yyTop]) == null) {
        yyVal = new ZArrayParseNode(position); /* zero length array */
    } else {
        yyVal = ((ParseNode)yyVals[-1+yyTop]);
    }
    return yyVal;
};
states[336] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((HashParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[337] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ReturnParseNode(((SourceIndexLength)yyVals[0+yyTop]), NilImplicitParseNode.NIL);
    return yyVal;
};
states[338] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_yield(((SourceIndexLength)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[339] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new YieldParseNode(((SourceIndexLength)yyVals[-2+yyTop]), null);
    return yyVal;
};
states[340] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new YieldParseNode(((SourceIndexLength)yyVals[0+yyTop]), null);
    return yyVal;
};
states[341] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_defined(((SourceIndexLength)yyVals[-4+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[342] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(support.getConditionNode(((ParseNode)yyVals[-1+yyTop])), TStringConstants.BANG);
    return yyVal;
};
states[343] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.getOperatorCallNode(NilImplicitParseNode.NIL, TStringConstants.BANG);
    return yyVal;
};
states[344] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.frobnicate_fcall_args(((FCallParseNode)yyVals[-1+yyTop]), null, ((IterParseNode)yyVals[0+yyTop]));
    yyVal = ((FCallParseNode)yyVals[-1+yyTop]);                    
    return yyVal;
};
states[346] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-1+yyTop]) != null && 
          ((BlockAcceptingParseNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassParseNode) {
          lexer.compile_error(PID.BLOCK_ARG_AND_BLOCK_GIVEN, "Both block arg and actual block given.");
    }
    yyVal = ((BlockAcceptingParseNode)yyVals[-1+yyTop]).setIterNode(((IterParseNode)yyVals[0+yyTop]));
    ((ParseNode)yyVal).extendPosition(((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[347] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((LambdaParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[348] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IfParseNode(((SourceIndexLength)yyVals[-5+yyTop]), support.getConditionNode(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[349] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IfParseNode(((SourceIndexLength)yyVals[-5+yyTop]), support.getConditionNode(((ParseNode)yyVals[-4+yyTop])), ((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[-2+yyTop]));
    return yyVal;
};
states[350] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().begin();
    return yyVal;
};
states[351] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().end();
    return yyVal;
};
states[352] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[-1+yyTop]));
    yyVal = new WhileParseNode(((SourceIndexLength)yyVals[-6+yyTop]), support.getConditionNode(((ParseNode)yyVals[-4+yyTop])), body);
    return yyVal;
};
states[353] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().begin();
    return yyVal;
};
states[354] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().end();
    return yyVal;
};
states[355] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[-1+yyTop]));
    yyVal = new UntilParseNode(((SourceIndexLength)yyVals[-6+yyTop]), support.getConditionNode(((ParseNode)yyVals[-4+yyTop])), body);
    return yyVal;
};
states[356] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newCaseNode(((SourceIndexLength)yyVals[-4+yyTop]), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[357] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newCaseNode(((SourceIndexLength)yyVals[-3+yyTop]), null, ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[358] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newCaseInNode(((SourceIndexLength)yyVals[-4+yyTop]), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[359] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().begin();
    return yyVal;
};
states[360] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().end();
    return yyVal;
};
states[361] = (support, lexer, yyVal, yyVals, yyTop) -> {
    /* ENEBO: Lots of optz in 1.9 parser here*/
  yyVal = new ForParseNode(((SourceIndexLength)yyVals[-8+yyTop]), ((ParseNode)yyVals[-7+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[-4+yyTop]), support.getCurrentScope());
    return yyVal;
};
states[362] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) {
        support.yyerror("class definition in method body");
    }
    support.pushLocalScope();
    yyVal = support.isInClass(); /* MRI reuses $1 but we use the value for position.*/
    support.setIsInClass(true);
    return yyVal;
};
states[363] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[-1+yyTop]));

    yyVal = new ClassParseNode(support.extendedUntil(((SourceIndexLength)yyVals[-5+yyTop]), lexer.getPosition()), ((Colon3ParseNode)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((ParseNode)yyVals[-3+yyTop]));
    support.popCurrentScope();
    support.setIsInClass(((Boolean)yyVals[-2+yyTop]).booleanValue());
    return yyVal;
};
states[364] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = (support.isInClass() ? 2 : 0) | (support.isInDef() ? 1 : 0);
    support.setInDef(false);
    support.setIsInClass(false);
    support.pushLocalScope();
    return yyVal;
};
states[365] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[-1+yyTop]));

    yyVal = new SClassParseNode(support.extendedUntil(((SourceIndexLength)yyVals[-6+yyTop]), lexer.getPosition()), ((ParseNode)yyVals[-4+yyTop]), support.getCurrentScope(), body);
    support.popCurrentScope();
    support.setInDef(((((Integer)yyVals[-3+yyTop]).intValue()) & 1) != 0);
    support.setIsInClass(((((Integer)yyVals[-3+yyTop]).intValue()) & 2) != 0);
    return yyVal;
};
states[366] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) { 
        support.yyerror("module definition in method body");
    }
    yyVal = support.isInClass();
    support.setIsInClass(true);
    support.pushLocalScope();
    return yyVal;
};
states[367] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[-1+yyTop]));

    yyVal = new ModuleParseNode(support.extendedUntil(((SourceIndexLength)yyVals[-4+yyTop]), lexer.getPosition()), ((Colon3ParseNode)yyVals[-3+yyTop]), support.getCurrentScope(), body);
    support.popCurrentScope();
    support.setIsInClass(((Boolean)yyVals[-2+yyTop]).booleanValue());
    return yyVal;
};
states[368] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* restore_defun(p, $<node>1->nd_defn);*/
		    /*%%%*/
			/* $$ = set_defun_body(p, $1, $2, $3, &@$);*/
    return yyVal;
};
states[369] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* restore_defun(p, $<node>1->nd_defn);*/
		    /*%%%*/
			/* $$ = set_defun_body(p, $1, $2, $3, &@$);*/
		    /*%
// $1 = get_value($1);
		    %*/
    return yyVal;
};
states[370] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.pushLocalScope();
    yyVal = lexer.getCurrentArg();
    lexer.setCurrentArg(null);
    support.checkMethodName(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[371] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.isInDef();
    support.setInDef(true);
    return yyVal;
};
states[372] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = support.makeNullNil(((ParseNode)yyVals[-1+yyTop]));

    yyVal = new DefnParseNode(support.extendedUntil(((SourceIndexLength)yyVals[-6+yyTop]), ((SourceIndexLength)yyVals[0+yyTop])), support.symbolID(((TruffleString)yyVals[-5+yyTop])), (ArgsParseNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
    support.popCurrentScope();
    support.setInDef(((Boolean)yyVals[-3+yyTop]).booleanValue());
    lexer.setCurrentArg(((TruffleString)yyVals[-4+yyTop]));
    return yyVal;
};
states[373] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_FNAME); 
    yyVal = support.isInDef();
    support.setInDef(true);
    return yyVal;
};
states[374] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.pushLocalScope();
    lexer.setState(EXPR_ENDFN|EXPR_LABEL); /* force for args */
    yyVal = lexer.getCurrentArg();
    lexer.setCurrentArg(null);
    support.checkMethodName(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[375] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode body = ((ParseNode)yyVals[-1+yyTop]);
    if (body == null) body = NilImplicitParseNode.NIL;

    yyVal = new DefsParseNode(support.extendedUntil(((SourceIndexLength)yyVals[-8+yyTop]), ((SourceIndexLength)yyVals[0+yyTop])), ((ParseNode)yyVals[-7+yyTop]), support.symbolID(((TruffleString)yyVals[-4+yyTop])), (ArgsParseNode) yyVals[-2+yyTop], support.getCurrentScope(), body);
    support.popCurrentScope();
    support.setInDef(((Boolean)yyVals[-5+yyTop]).booleanValue());
    lexer.setCurrentArg(((TruffleString)yyVals[-3+yyTop]));
    return yyVal;
};
states[376] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BreakParseNode(((SourceIndexLength)yyVals[0+yyTop]), NilImplicitParseNode.NIL);
    return yyVal;
};
states[377] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new NextParseNode(((SourceIndexLength)yyVals[0+yyTop]), NilImplicitParseNode.NIL);
    return yyVal;
};
states[378] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new RedoParseNode(((SourceIndexLength)yyVals[0+yyTop]));
    return yyVal;
};
states[379] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new RetryParseNode(((SourceIndexLength)yyVals[0+yyTop]));
    return yyVal;
};
states[380] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    if (yyVal == null) yyVal = NilImplicitParseNode.NIL;
    return yyVal;
};
states[381] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((SourceIndexLength)yyVals[0+yyTop]);
    return yyVal;
};
states[382] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((SourceIndexLength)yyVals[0+yyTop]);
    return yyVal;
};
states[383] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* token_info_push(p, "def", &@$);*/
			support.setInArgDef(true);
			/* p->ctxt.in_argdef = 1;*/
    return yyVal;
};
states[384] = (support, lexer, yyVal, yyVals, yyTop) -> {
			/* token_info_pop(p, "end", &@$);*/
    return yyVal;
};
states[385] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInClass() && !support.isInDef() && !support.getCurrentScope().isBlockScope()) {
        lexer.compile_error(PID.TOP_LEVEL_RETURN, "Invalid return in class/module body");
    }
    yyVal = ((SourceIndexLength)yyVals[0+yyTop]);
    return yyVal;
};
states[392] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IfParseNode(((SourceIndexLength)yyVals[-4+yyTop]), support.getConditionNode(((ParseNode)yyVals[-3+yyTop])), ((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[394] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[396] = (support, lexer, yyVal, yyVals, yyTop) -> yyVal;
states[397] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.assignableInCurr(((TruffleString)yyVals[0+yyTop]), NilImplicitParseNode.NIL);
    return yyVal;
};
states[398] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[399] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newArrayNode(((ParseNode)yyVals[0+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[400] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[401] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[0+yyTop]).getPosition(), ((ListParseNode)yyVals[0+yyTop]), null, null);
    return yyVal;
};
states[402] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), support.assignableInCurr(((TruffleString)yyVals[0+yyTop]), null), null);
    return yyVal;
};
states[403] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), support.assignableInCurr(((TruffleString)yyVals[-2+yyTop]), null), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[404] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-2+yyTop]).getPosition(), ((ListParseNode)yyVals[-2+yyTop]), new StarParseNode(lexer.getPosition()), null);
    return yyVal;
};
states[405] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(((ListParseNode)yyVals[-4+yyTop]).getPosition(), ((ListParseNode)yyVals[-4+yyTop]), new StarParseNode(lexer.getPosition()), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[406] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(lexer.getPosition(), null, support.assignableInCurr(((TruffleString)yyVals[0+yyTop]), null), null);
    return yyVal;
};
states[407] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(lexer.getPosition(), null, support.assignableInCurr(((TruffleString)yyVals[-2+yyTop]), null), ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[408] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(lexer.getPosition(), null, new StarParseNode(lexer.getPosition()), null);
    return yyVal;
};
states[409] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new MultipleAsgnParseNode(support.getPosition(((ListParseNode)yyVals[0+yyTop])), null, null, ((ListParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[410] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[411] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]), (TruffleString) null, ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[412] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(lexer.getPosition(), null, ((TruffleString)yyVals[-1+yyTop]), ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[413] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(lexer.getPosition(), null, RubyLexer.Keyword.NIL.bytes, ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[414] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(((BlockArgParseNode)yyVals[0+yyTop]).getPosition(), null, (TruffleString) null, ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[415] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
    return yyVal;
};
states[416] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(lexer.getPosition(), null, (TruffleString) null, null);
    return yyVal;
};
states[417] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), ((ListParseNode)yyVals[-3+yyTop]), ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[418] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-7+yyTop]).getPosition(), ((ListParseNode)yyVals[-7+yyTop]), ((ListParseNode)yyVals[-5+yyTop]), ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[419] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[420] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), ((ListParseNode)yyVals[-3+yyTop]), null, ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[421] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), null, ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[422] = (support, lexer, yyVal, yyVals, yyTop) -> {
    RestArgParseNode rest = new UnnamedRestArgParseNode(((ListParseNode)yyVals[-1+yyTop]).getPosition(), Layouts.TEMP_PREFIX + "anonymous_rest", support.getCurrentScope().addVariable("*"), false);
    yyVal = support.new_args(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]), null, rest, null, (ArgsTailHolder) null);
    return yyVal;
};
states[423] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), null, ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[424] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[425] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(support.getPosition(((ListParseNode)yyVals[-3+yyTop])), null, ((ListParseNode)yyVals[-3+yyTop]), ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[426] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(support.getPosition(((ListParseNode)yyVals[-5+yyTop])), null, ((ListParseNode)yyVals[-5+yyTop]), ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[427] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(support.getPosition(((ListParseNode)yyVals[-1+yyTop])), null, ((ListParseNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[428] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), null, ((ListParseNode)yyVals[-3+yyTop]), null, ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[429] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((RestArgParseNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[430] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((RestArgParseNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[431] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[432] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
    return yyVal;
};
states[433] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.enterBlockParameters();
    return yyVal;
};
states[434] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.commandStart = true;
    yyVal = ((ArgsParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[435] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
    return yyVal;
};
states[436] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
    return yyVal;
};
states[437] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = ((ArgsParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[438] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[439] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[440] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[441] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[442] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.new_bv(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[443] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[444] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.pushBlockScope();
    yyVal = lexer.getLeftParenBegin();
    lexer.setLeftParenBegin(lexer.incrementParenNest());
    return yyVal;
};
states[445] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = Long.valueOf(lexer.getCmdArgumentState().getStack());
    lexer.getCmdArgumentState().reset();
    return yyVal;
};
states[446] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
    lexer.getCmdArgumentState().restart();
    yyVal = new LambdaParseNode(((ArgsParseNode)yyVals[-2+yyTop]).getPosition(), ((ArgsParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]), support.getCurrentScope());
    lexer.setLeftParenBegin(((Integer)yyVals[-3+yyTop]));
    support.popCurrentScope();
    return yyVal;
};
states[447] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.enterBlockParameters();
    return yyVal;
};
states[448] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ArgsParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[449] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.enterBlockParameters();
    return yyVal;
};
states[450] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ArgsParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[451] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, null);
    return yyVal;
};
states[452] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[453] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[454] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((IterParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[455] = (support, lexer, yyVal, yyVals, yyTop) -> {
    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
    if (((ParseNode)yyVals[-1+yyTop]) instanceof YieldParseNode) {
        lexer.compile_error(PID.BLOCK_GIVEN_TO_YIELD, "block given to yield");
    }
    if (((ParseNode)yyVals[-1+yyTop]) instanceof BlockAcceptingParseNode && ((BlockAcceptingParseNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassParseNode) {
        lexer.compile_error(PID.BLOCK_ARG_AND_BLOCK_GIVEN, "Both block arg and actual block given.");
    }
    if (((ParseNode)yyVals[-1+yyTop]) instanceof NonLocalControlFlowParseNode) {
        ((BlockAcceptingParseNode) ((NonLocalControlFlowParseNode)yyVals[-1+yyTop]).getValueNode()).setIterNode(((IterParseNode)yyVals[0+yyTop]));
    } else {
        ((BlockAcceptingParseNode)yyVals[-1+yyTop]).setIterNode(((IterParseNode)yyVals[0+yyTop]));
    }
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    ((ParseNode)yyVal).extendPosition(((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[456] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[457] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((IterParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[458] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-4+yyTop]), ((TruffleString)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((IterParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[459] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.frobnicate_fcall_args(((FCallParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    yyVal = ((FCallParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[460] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[461] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[462] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[0+yyTop]), null, null);
    return yyVal;
};
states[463] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-2+yyTop]), ((TruffleString)yyVals[-1+yyTop]), TStringConstants.CALL, ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[464] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_call(((ParseNode)yyVals[-2+yyTop]), TStringConstants.CALL, ((ParseNode)yyVals[0+yyTop]), null);
    return yyVal;
};
states[465] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_super(((SourceIndexLength)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[466] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ZSuperParseNode(((SourceIndexLength)yyVals[0+yyTop]));
    return yyVal;
};
states[467] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-3+yyTop]) instanceof SelfParseNode) {
        yyVal = support.new_fcall(TStringConstants.LBRACKET_RBRACKET);
        support.frobnicate_fcall_args(((FCallParseNode)yyVal), ((ParseNode)yyVals[-1+yyTop]), null);
    } else {
        yyVal = support.new_call(((ParseNode)yyVals[-3+yyTop]), TStringConstants.LBRACKET_RBRACKET, ((ParseNode)yyVals[-1+yyTop]), null);
    }
    return yyVal;
};
states[468] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((IterParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[469] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((IterParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[470] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getPosition();
    return yyVal;
};
states[471] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.pushBlockScope();
    yyVal = Long.valueOf(lexer.getCmdArgumentState().getStack()) >> 1;
    lexer.getCmdArgumentState().reset();
    return yyVal;
};
states[472] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IterParseNode(((SourceIndexLength)yyVals[-3+yyTop]), ((ArgsParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), support.getCurrentScope());
     support.popCurrentScope();
    lexer.getCmdArgumentState().reset(((Long)yyVals[-2+yyTop]).longValue());
    return yyVal;
};
states[473] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getPosition();
    return yyVal;
};
states[474] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.pushBlockScope();
    yyVal = Long.valueOf(lexer.getCmdArgumentState().getStack());
    lexer.getCmdArgumentState().reset();
    return yyVal;
};
states[475] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new IterParseNode(((SourceIndexLength)yyVals[-3+yyTop]), ((ArgsParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]), support.getCurrentScope());
     support.popCurrentScope();
    lexer.getCmdArgumentState().reset(((Long)yyVals[-2+yyTop]).longValue());
    return yyVal;
};
states[476] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newWhenNode(((SourceIndexLength)yyVals[-4+yyTop]), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[479] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newInNode(((SourceIndexLength)yyVals[-4+yyTop]), ((ParseNode)yyVals[-3+yyTop]), ((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[482] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode node;
    if (((ParseNode)yyVals[-3+yyTop]) != null) {
        node = support.appendToBlock(support.node_assign(((ParseNode)yyVals[-3+yyTop]), new GlobalVarParseNode(((SourceIndexLength)yyVals[-5+yyTop]), support.symbolID(TStringConstants.DOLLAR_BANG))), ((ParseNode)yyVals[-1+yyTop]));
        if (((ParseNode)yyVals[-1+yyTop]) != null) {
            node.extendPosition(((SourceIndexLength)yyVals[-5+yyTop]));
        }
    } else {
        node = ((ParseNode)yyVals[-1+yyTop]);
    }
    ParseNode body = support.makeNullNil(node);
    yyVal = new RescueBodyParseNode(((SourceIndexLength)yyVals[-5+yyTop]), ((ParseNode)yyVals[-4+yyTop]), body, ((RescueBodyParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[483] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null; 
    return yyVal;
};
states[484] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newArrayNode(((ParseNode)yyVals[0+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[485] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.splat_array(((ParseNode)yyVals[0+yyTop]));
    if (yyVal == null) yyVal = ((ParseNode)yyVals[0+yyTop]); /* ArgsCat or ArgsPush*/
    return yyVal;
};
states[487] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[489] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[491] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((NumericParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[492] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.asSymbol(lexer.getPosition(), ((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[494] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]) instanceof EvStrParseNode ? new DStrParseNode(((ParseNode)yyVals[0+yyTop]).getPosition(), lexer.getEncoding()).add(((ParseNode)yyVals[0+yyTop])) : ((ParseNode)yyVals[0+yyTop]);
    /*
    NODE *node = $1;
    if (!node) {
        node = NEW_STR(STR_NEW0());
    } else {
        node = evstr2dstr(node);
    }
    $$ = node;
    */
    return yyVal;
};
states[495] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((StrParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[496] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[497] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.literal_concat(((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[498] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.heredoc_dedent(((ParseNode)yyVals[-1+yyTop]));
    lexer.setHeredocIndent(0);
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[499] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength position = support.getPosition(((ParseNode)yyVals[-1+yyTop]));

    lexer.heredoc_dedent(((ParseNode)yyVals[-1+yyTop]));
    lexer.setHeredocIndent(0);

    if (((ParseNode)yyVals[-1+yyTop]) == null) {
        yyVal = new XStrParseNode(position, null);
    } else if (((ParseNode)yyVals[-1+yyTop]) instanceof StrParseNode) {
        yyVal = new XStrParseNode(position, ((StrParseNode)yyVals[-1+yyTop]));
    } else if (((ParseNode)yyVals[-1+yyTop]) instanceof DStrParseNode) {
        yyVal = new DXStrParseNode(position, ((DStrParseNode)yyVals[-1+yyTop]));
    } else {
        yyVal = new DXStrParseNode(position).add(((ParseNode)yyVals[-1+yyTop]));
    }
    return yyVal;
};
states[500] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.newRegexpNode(support.getPosition(((ParseNode)yyVals[-1+yyTop])), ((ParseNode)yyVals[-1+yyTop]), ((RegexpParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[501] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[502] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(lexer.getPosition());
    return yyVal;
};
states[503] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[-1+yyTop]) instanceof EvStrParseNode ? new DStrParseNode(((ListParseNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((ParseNode)yyVals[-1+yyTop])) : ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[504] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[505] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.literal_concat(((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[506] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[507] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(lexer.getPosition());
    return yyVal;
};
states[508] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[-1+yyTop]) instanceof EvStrParseNode ? new DSymbolParseNode(((ListParseNode)yyVals[-2+yyTop]).getPosition()).add(((ParseNode)yyVals[-1+yyTop])) : support.asSymbol(((ListParseNode)yyVals[-2+yyTop]).getPosition(), ((ParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[509] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[510] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[511] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(lexer.getPosition());
    return yyVal;
};
states[512] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[513] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(lexer.getPosition());
    return yyVal;
};
states[514] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(support.asSymbol(((ListParseNode)yyVals[-2+yyTop]).getPosition(), ((ParseNode)yyVals[-1+yyTop])));
    return yyVal;
};
states[515] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.createStr(lexer.encoding.tencoding.getEmpty(), lexer.encoding, 0);
    return yyVal;
};
states[516] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.literal_concat(((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[517] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[518] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.literal_concat(((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[519] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[520] = (support, lexer, yyVal, yyVals, yyTop) -> {
    /* FIXME: mri is different here.*/
                    yyVal = support.literal_concat(((ParseNode)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[521] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[522] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getStrTerm();
    lexer.setStrTerm(null);
    lexer.setState(EXPR_BEG);
    return yyVal;
};
states[523] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
    yyVal = new EvStrParseNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[524] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getStrTerm();
    lexer.setStrTerm(null);
    lexer.getConditionState().stop();
    return yyVal;
};
states[525] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getCmdArgumentState().getStack();
    lexer.getCmdArgumentState().reset();
    return yyVal;
};
states[526] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getState();
    lexer.setState(EXPR_BEG);
    return yyVal;
};
states[527] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getBraceNest();
    lexer.setBraceNest(0);
    return yyVal;
};
states[528] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.getHeredocIndent();
    lexer.setHeredocIndent(0);
    return yyVal;
};
states[529] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.getConditionState().restart();
    lexer.setStrTerm(((StrTerm)yyVals[-6+yyTop]));
    lexer.getCmdArgumentState().reset(((Long)yyVals[-5+yyTop]).longValue());
    lexer.setState(((Integer)yyVals[-4+yyTop]));
    lexer.setBraceNest(((Integer)yyVals[-3+yyTop]));
    lexer.setHeredocIndent(((Integer)yyVals[-2+yyTop]));
    lexer.setHeredocLineIndent(-1);

    yyVal = support.newEvStrNode(support.getPosition(((ParseNode)yyVals[-1+yyTop])), ((ParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[530] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new GlobalVarParseNode(lexer.getPosition(), support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[531] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new InstVarParseNode(lexer.getPosition(), support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[532] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ClassVarParseNode(lexer.getPosition(), support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[534] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_END|EXPR_ENDARG);
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[536] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[537] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[538] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[539] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_END|EXPR_ENDARG);

    /* DStrNode: :"some text #{some expression}"*/
    /* StrNode: :"some text"*/
    /* EvStrNode :"#{some expression}"*/
    /* Ruby 1.9 allows empty strings as symbols*/
    if (((ParseNode)yyVals[-1+yyTop]) == null) {
        yyVal = support.asSymbol(lexer.getPosition(), TStringConstants.EMPTY_US_ASCII);
    } else if (((ParseNode)yyVals[-1+yyTop]) instanceof DStrParseNode) {
        yyVal = new DSymbolParseNode(((ParseNode)yyVals[-1+yyTop]).getPosition(), ((DStrParseNode)yyVals[-1+yyTop]));
    } else if (((ParseNode)yyVals[-1+yyTop]) instanceof StrParseNode) {
        yyVal = support.asSymbol(((ParseNode)yyVals[-1+yyTop]).getPosition(), ((ParseNode)yyVals[-1+yyTop]));
    } else {
        yyVal = new DSymbolParseNode(((ParseNode)yyVals[-1+yyTop]).getPosition());
        ((DSymbolParseNode)yyVal).add(((ParseNode)yyVals[-1+yyTop]));
    }
    return yyVal;
};
states[540] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((NumericParseNode)yyVals[0+yyTop]);  
    return yyVal;
};
states[541] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.negateNumeric(((NumericParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[542] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[543] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((FloatParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[544] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((RationalParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[545] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[546] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.declareIdentifier(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[547] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new InstVarParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[548] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new GlobalVarParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[549] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ConstParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[550] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ClassVarParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])));
    return yyVal;
};
states[551] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new NilParseNode(lexer.tokline);
    return yyVal;
};
states[552] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new SelfParseNode(lexer.tokline);
    return yyVal;
};
states[553] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new TrueParseNode((SourceIndexLength) yyVal);
    return yyVal;
};
states[554] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new FalseParseNode((SourceIndexLength) yyVal);
    return yyVal;
};
states[555] = (support, lexer, yyVal, yyVals, yyTop) -> {
    RubyEncoding encoding = support.getConfiguration().getContext() == null ? Encodings.UTF_8 : support.getConfiguration().getContext().getEncodingManager().getLocaleEncoding();
    yyVal = new FileParseNode(lexer.tokline, TStringUtils.fromJavaString(lexer.getFile(), encoding), encoding);
    return yyVal;
};
states[556] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new FixnumParseNode(lexer.tokline, lexer.tokline.toSourceSection(lexer.getSource()).getStartLine() + lexer.getLineOffset());
    return yyVal;
};
states[557] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new EncodingParseNode(lexer.tokline, lexer.getEncoding());
    return yyVal;
};
states[558] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.assignableLabelOrIdentifier(((TruffleString)yyVals[0+yyTop]), null);
    return yyVal;
};
states[559] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new InstAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[560] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new GlobalAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[561] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (support.isInDef()) support.compile_error("dynamic constant assignment");

    yyVal = new ConstDeclParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), null, NilImplicitParseNode.NIL);
    return yyVal;
};
states[562] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ClassVarAsgnParseNode(lexer.tokline, support.symbolID(((TruffleString)yyVals[0+yyTop])), NilImplicitParseNode.NIL);
    return yyVal;
};
states[563] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to nil");
    yyVal = null;
    return yyVal;
};
states[564] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't change the value of self");
    yyVal = null;
    return yyVal;
};
states[565] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to true");
    yyVal = null;
    return yyVal;
};
states[566] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to false");
    yyVal = null;
    return yyVal;
};
states[567] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __FILE__");
    yyVal = null;
    return yyVal;
};
states[568] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __LINE__");
    yyVal = null;
    return yyVal;
};
states[569] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.compile_error("Can't assign to __ENCODING__");
    yyVal = null;
    return yyVal;
};
states[570] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[571] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[572] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_BEG);
    lexer.commandStart = true;
    return yyVal;
};
states[573] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[574] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[576] = (support, lexer, yyVal, yyVals, yyTop) -> {
			support.setInArgDef(false);
			ArgsTailHolder argsTail = support.new_args_tail(lexer.getPosition(), null, null, null);
			yyVal = support.new_args(((ParseNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, argsTail);

			/* p->ctxt.in_argdef = 0;*/
			/* $$ = new_args_tail(p, Qnone, Qnone, Qnone, &@0);*/
			/* $$ = new_args(p, Qnone, Qnone, Qnone, Qnone, $$, &@0);*/
    return yyVal;
};
states[577] = (support, lexer, yyVal, yyVals, yyTop) -> {
			yyVal = ((ArgsParseNode)yyVals[-1+yyTop]);
			lexer.setState(EXPR_BEG);
			lexer.commandStart = true;
			support.setInArgDef(false);

			/* SET_LEX_STATE(EXPR_BEG);*/
			/* p->command_start = TRUE;*/
			/* p->ctxt.in_argdef = 0;*/
    return yyVal;
};
states[578] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ArgsParseNode)yyVals[-1+yyTop]);
    lexer.setState(EXPR_BEG);
    lexer.commandStart = true;
    return yyVal;
};
states[579] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = lexer.inKwarg;
    lexer.inKwarg = true;
    lexer.setState(lexer.getState() | EXPR_LABEL);
    return yyVal;
};
states[580] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.inKwarg = ((Boolean)yyVals[-2+yyTop]);
     yyVal = ((ArgsParseNode)yyVals[-1+yyTop]);
     lexer.setState(EXPR_BEG);
     lexer.commandStart = true;
    return yyVal;
};
states[581] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), ((TruffleString)yyVals[-1+yyTop]), ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[582] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]), (TruffleString) null, ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[583] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(lexer.getPosition(), null, ((TruffleString)yyVals[-1+yyTop]), ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[584] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(lexer.getPosition(), null, RubyLexer.Keyword.NIL.bytes, ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[585] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(((BlockArgParseNode)yyVals[0+yyTop]).getPosition(), null, (TruffleString) null, ((BlockArgParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[586] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
    return yyVal;
};
states[587] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args_tail(lexer.getPosition(), null, (TruffleString) null, null);
    return yyVal;
};
states[588] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ArgsParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[589] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
    return yyVal;
};
states[590] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), ((ListParseNode)yyVals[-3+yyTop]), ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[591] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-7+yyTop]).getPosition(), ((ListParseNode)yyVals[-7+yyTop]), ((ListParseNode)yyVals[-5+yyTop]), ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[592] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[593] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), ((ListParseNode)yyVals[-3+yyTop]), null, ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[594] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), ((ListParseNode)yyVals[-3+yyTop]), null, ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[595] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), ((ListParseNode)yyVals[-5+yyTop]), null, ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[596] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-1+yyTop]).getPosition(), ((ListParseNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[597] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), null, ((ListParseNode)yyVals[-3+yyTop]), ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[598] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-5+yyTop]).getPosition(), null, ((ListParseNode)yyVals[-5+yyTop]), ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[599] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-1+yyTop]).getPosition(), null, ((ListParseNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[600] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ListParseNode)yyVals[-3+yyTop]).getPosition(), null, ((ListParseNode)yyVals[-3+yyTop]), null, ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[601] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((RestArgParseNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgParseNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[602] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((RestArgParseNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgParseNode)yyVals[-3+yyTop]), ((ListParseNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[603] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
    return yyVal;
};
states[604] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength position = support.getPosition(null);
    RestArgParseNode splat = new RestArgParseNode(position, ParserSupport.FORWARD_ARGS_REST_VAR, 0);
    BlockArgParseNode block = new BlockArgParseNode(position, 1, ParserSupport.FORWARD_ARGS_BLOCK_VAR);
    ArgsTailHolder argsTail = support.new_args_tail(position, null, ParserSupport.FORWARD_ARGS_KWREST_VAR_TSTRING, block);
    yyVal = support.new_args(position, ((ListParseNode)yyVals[-2+yyTop]), null, splat, null, argsTail);
    return yyVal;
};
states[605] = (support, lexer, yyVal, yyVals, yyTop) -> {
    SourceIndexLength position = support.getPosition(null);
    RestArgParseNode splat = new RestArgParseNode(position, ParserSupport.FORWARD_ARGS_REST_VAR, 0);
    BlockArgParseNode block = new BlockArgParseNode(position, 1, ParserSupport.FORWARD_ARGS_BLOCK_VAR);
    ArgsTailHolder argsTail = support.new_args_tail(position, null, ParserSupport.FORWARD_ARGS_KWREST_VAR_TSTRING, block);
    yyVal = support.new_args(position, null, null, splat, null, argsTail);
    return yyVal;
};
states[607] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("formal argument cannot be a constant");
    return yyVal;
};
states[608] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("formal argument cannot be an instance variable");
    return yyVal;
};
states[609] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("formal argument cannot be a global variable");
    return yyVal;
};
states[610] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.yyerror("formal argument cannot be a class variable");
    return yyVal;
};
states[611] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]); /* Not really reached*/
    return yyVal;
};
states[612] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.formal_argument(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[613] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(((TruffleString)yyVals[0+yyTop]));
    yyVal = support.arg_var(((TruffleString)yyVals[0+yyTop]));
    return yyVal;
};
states[614] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = ((ArgumentParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[615] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    /*            {
            ID tid = internal_id();
            arg_var(tid);
            if (dyna_in_block()) {
$2->nd_value = NEW_DVAR(tid);
            }
            else {
$2->nd_value = NEW_LVAR(tid);
            }
            $$ = NEW_ARGS_AUX(tid, 1);
            $$->nd_next = $2;*/
    return yyVal;
};
states[616] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(lexer.getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[617] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[0+yyTop]));
    yyVal = ((ListParseNode)yyVals[-2+yyTop]);
    return yyVal;
};
states[618] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.arg_var(support.formal_argument(((TruffleString)yyVals[0+yyTop])));
    lexer.setCurrentArg(((TruffleString)yyVals[0+yyTop]));
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[619] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = support.keyword_arg(((ParseNode)yyVals[0+yyTop]).getPosition(), support.assignableKeyword(((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[620] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = support.keyword_arg(lexer.getPosition(), support.assignableKeyword(((TruffleString)yyVals[0+yyTop]), RequiredKeywordArgumentValueParseNode.INSTANCE));
    return yyVal;
};
states[621] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.keyword_arg(support.getPosition(((ParseNode)yyVals[0+yyTop])), support.assignableKeyword(((TruffleString)yyVals[-1+yyTop]), ((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[622] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.keyword_arg(lexer.getPosition(), support.assignableKeyword(((TruffleString)yyVals[0+yyTop]), RequiredKeywordArgumentValueParseNode.INSTANCE));
    return yyVal;
};
states[623] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(((ParseNode)yyVals[0+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[624] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[625] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new ArrayParseNode(((ParseNode)yyVals[0+yyTop]).getPosition(), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[626] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((ListParseNode)yyVals[-2+yyTop]).add(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[627] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[628] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[630] = (support, lexer, yyVal, yyVals, yyTop) -> {
    support.shadowing_lvar(((TruffleString)yyVals[0+yyTop]));
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[631] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ParserSupport.INTERNAL_ID;
    return yyVal;
};
states[632] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = new OptArgParseNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((ArgumentParseNode)yyVals[-2+yyTop]).getName(), ((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[633] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setCurrentArg(null);
    yyVal = new OptArgParseNode(support.getPosition(((ParseNode)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((ArgumentParseNode)yyVals[-2+yyTop]).getName(), ((ParseNode)yyVals[0+yyTop])));
    return yyVal;
};
states[634] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BlockParseNode(((ParseNode)yyVals[0+yyTop]).getPosition()).add(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[635] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.appendToBlock(((ListParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[636] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BlockParseNode(((ParseNode)yyVals[0+yyTop]).getPosition()).add(((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[637] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.appendToBlock(((ListParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[638] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[639] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[640] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (!support.is_local_id(((TruffleString)yyVals[0+yyTop]))) {
        support.yyerror("rest argument must be local variable");
    }
                    
    yyVal = new RestArgParseNode(support.arg_var(support.shadowing_lvar(((TruffleString)yyVals[0+yyTop]))));
    return yyVal;
};
states[641] = (support, lexer, yyVal, yyVals, yyTop) -> {
  /* FIXME: bytelist_love: somewhat silly to remake the empty bytelist over and over but this type should change (using null vs "" is a strange distinction).*/
  yyVal = new UnnamedRestArgParseNode(lexer.getPosition(), Layouts.TEMP_PREFIX + "unnamed_rest", support.getCurrentScope().addVariable("*"), true);
    return yyVal;
};
states[642] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[643] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[644] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (!support.is_local_id(((TruffleString)yyVals[0+yyTop]))) {
        support.yyerror("block argument must be local variable");
    }
                    
    yyVal = new BlockArgParseNode(support.arg_var(support.shadowing_lvar(((TruffleString)yyVals[0+yyTop]))));
    return yyVal;
};
states[645] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new BlockArgParseNode(support.arg_var(support.shadowing_lvar(ParserSupport.FORWARD_ARGS_BLOCK_VAR_TSTRING)));
    return yyVal;
};
states[646] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((BlockArgParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[647] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[648] = (support, lexer, yyVal, yyVals, yyTop) -> {
    value_expr(lexer, ((ParseNode)yyVals[0+yyTop]));
    yyVal = ((ParseNode)yyVals[0+yyTop]);
    return yyVal;
};
states[649] = (support, lexer, yyVal, yyVals, yyTop) -> {
    lexer.setState(EXPR_BEG);
    return yyVal;
};
states[650] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-1+yyTop]) == null) {
        support.yyerror("can't define single method for ().");
    } else if (((ParseNode)yyVals[-1+yyTop]) instanceof ILiteralNode) {
        support.yyerror("can't define single method for literals.");
    }
    value_expr(lexer, ((ParseNode)yyVals[-1+yyTop]));
    yyVal = ((ParseNode)yyVals[-1+yyTop]);
    return yyVal;
};
states[651] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new HashParseNode(lexer.getPosition());
    return yyVal;
};
states[652] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.remove_duplicate_keys(((HashParseNode)yyVals[-1+yyTop]));
    return yyVal;
};
states[653] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = new HashParseNode(lexer.getPosition(), ((ParseNodeTuple)yyVals[0+yyTop]));
    return yyVal;
};
states[654] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((HashParseNode)yyVals[-2+yyTop]).add(((ParseNodeTuple)yyVals[0+yyTop]));
    return yyVal;
};
states[655] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.createKeyValue(((ParseNode)yyVals[-2+yyTop]), ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[656] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode label = support.asSymbol(support.getPosition(((ParseNode)yyVals[0+yyTop])), ((TruffleString)yyVals[-1+yyTop]));
    yyVal = support.createKeyValue(label, ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[657] = (support, lexer, yyVal, yyVals, yyTop) -> {
    ParseNode val = support.declareIdentifier(((TruffleString)yyVals[0+yyTop]));
    ParseNode label = support.asSymbol(support.getPosition(null), ((TruffleString)yyVals[0+yyTop]));
    yyVal = support.createKeyValue(label, val);
    return yyVal;
};
states[658] = (support, lexer, yyVal, yyVals, yyTop) -> {
    if (((ParseNode)yyVals[-2+yyTop]) instanceof StrParseNode) {
        DStrParseNode dnode = new DStrParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), lexer.getEncoding());
        dnode.add(((ParseNode)yyVals[-2+yyTop]));
        yyVal = support.createKeyValue(new DSymbolParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), dnode), ((ParseNode)yyVals[0+yyTop]));
    } else if (((ParseNode)yyVals[-2+yyTop]) instanceof DStrParseNode) {
        yyVal = support.createKeyValue(new DSymbolParseNode(support.getPosition(((ParseNode)yyVals[-2+yyTop])), ((DStrParseNode)yyVals[-2+yyTop])), ((ParseNode)yyVals[0+yyTop]));
    } else {
        support.compile_error("Uknown type for assoc in strings: " + ((ParseNode)yyVals[-2+yyTop]));
    }

    return yyVal;
};
states[659] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = support.createKeyValue(null, ((ParseNode)yyVals[0+yyTop]));
    return yyVal;
};
states[660] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[661] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[662] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[663] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[664] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[665] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[666] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[667] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[668] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[669] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[670] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[671] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[672] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[673] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[675] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[680] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[681] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = ((TruffleString)yyVals[0+yyTop]);
    return yyVal;
};
states[689] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
states[690] = (support, lexer, yyVal, yyVals, yyTop) -> {
    yyVal = null;
    return yyVal;
};
}
// line 3026 "RubyParser.y"

    /** The parse method use an lexer stream and parse it to an AST node 
     * structure
     */
    public RubyParserResult parse(ParserConfiguration configuration) {
        support.reset();
        support.setConfiguration(configuration);
        support.setResult(new RubyParserResult());
        
        yyparse(lexer, null);
        
        return support.getResult();
    }
}
// CheckStyle: stop generated
// @formatter:on
// line 11313 "-"
