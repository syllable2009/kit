package com.jxp.ct;

import lombok.extern.slf4j.Slf4j;
import xyz.erupt.linq.Linq;
import xyz.erupt.linq.lambda.Th;

/**
 * @author jiaxiaopeng
 * Created on 2025-02-10 16:15
 */
@Slf4j
public class LinqTest {
    @SuppressWarnings("checkstyle:")
    public static void main(String[] args) {
        int a = 10000;
        var strings = Linq.from("C", "A", "B", "B").gt(Th::is, "A").orderByDesc(Th::is).write(String.class);
// [C, B, B]
        log.info("strings:{}", strings);
        var integers = Linq.from(1, 2, 3, 7, 6, 5).orderByDesc(Th::is).write(Integer.class);
// [1, 2, 3, 5, 6, 7]
        log.info("integers:{}", integers);
    }

//    var name = Linq.from(data)
//            // left join
//            .innerJoin(target, Target::getId, Data::getId)
//            // where like
//            .like(Data::getName, "a")
//            // select name
//            .select(Data::getName)
//            // distinct
//            .distinct()
//            // order by
//            .orderBy(Data::getName)
//            .write(String.class);



}
