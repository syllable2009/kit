package com.jxp.component.echarts;

import java.io.IOException;

import javax.annotation.Resource;

import org.icepear.echarts.Line;
import org.icepear.echarts.charts.line.LineAreaStyle;
import org.icepear.echarts.charts.line.LineSeries;
import org.icepear.echarts.components.coord.cartesian.CategoryAxis;
import org.icepear.echarts.render.Engine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-13 14:41
 */
@Slf4j
@RestController
public class EchartsApi {

    @Resource
    private Engine engine;
    @Resource
    private Handlebars handlebars;

    @GetMapping("/linechart")
    public ResponseEntity<String> getChart() {
        Line lineChart = new Line()
                .addXAxis(new CategoryAxis()
                        .setData(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"})
                        .setBoundaryGap(false))
                .addYAxis()
                .addSeries(new LineSeries()
                        .setData(new Number[]{820, 932, 901, 934, 1290, 1330, 1320})
                        .setAreaStyle(new LineAreaStyle()));
        String json = engine.renderHtml(lineChart);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/option")
    public String index() {
        Line lineChart = new Line()
                .addXAxis(new CategoryAxis()
                        .setData(new String[] { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" })
                        .setBoundaryGap(false))
                .addYAxis()
                .addSeries(new LineSeries()
                        .setData(new Number[] { 820, 932, 901, 934, 1290, 1330, 1320 })
                        .setAreaStyle(new LineAreaStyle()));

        String html = "";
        try {
            Template template = handlebars.compile("templates/index");
            html = template.apply(engine.renderJsonOption(lineChart));
        } catch (IOException e) {
            System.out.println("template file not found");
        }
        return html;
    }
}
