package com.jxp.md;

import java.util.Arrays;

import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-01-10 15:45
 */
@Slf4j
public class FFFFF {

    public static void main(String[] args) {

        DataHolder options = new MutableDataSet()
                .set(Parser.EXTENSIONS, Arrays.asList(
                        DefinitionExtension.create(),
                        EmojiExtension.create(),
                        FootnoteExtension.create(),
                        StrikethroughSubscriptExtension.create(),
                        InsExtension.create(),
                        SuperscriptExtension.create(),
                        TablesExtension.create(),
                        TocExtension.create(),
                        SimTocExtension.create(),
                        WikiLinkExtension.create()
                ));
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse("This is *Sparta*");
        TextCollectingVisitor textCollector = new TextCollectingVisitor();
        String text = textCollector.collectAndGetText(document);
        log.info("{}", text);
    }
}
