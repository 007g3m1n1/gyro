parser grammar BeamNewParser;

options
   { tokenVocab = BeamNewLexer; }
/*
 * Parser Rules
 */

beamRoot
    : NEWLINE* globalScope EOF
    ;

globalScope
    : config*
    ;

config
    : extension? param+ (configBlock)? (lineSeparator | EOF)
    ;

lineSeparator
    : NEWLINE+
    ;

extension
    : TOKEN
    ;

param
    : value
    ;

configBlock
    : completeBlock | simpleBlock
    ;

completeBlock
    : COLON lineSeparator (config* | listEntry*) END
    ;

listEntry
    : DASH value lineSeparator
    ;

simpleBlock
    : COLON value
    ;

value
    : (TOKEN | LITERAL | reference) (DASH | TOKEN | LITERAL | reference)*
    ;

reference
    : resourceReference
    ;

resourceReference
    : AT LPAREN referenceType? referenceName (PIPE referenceChain)? RPAREN
    ;

referenceType
    : TOKEN
    ;

referenceName
    : TOKEN
    ;

referenceChain
    : TOKEN
    ;
