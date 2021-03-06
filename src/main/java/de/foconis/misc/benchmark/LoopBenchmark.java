package de.foconis.misc.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
public class LoopBenchmark {

    private final List<Integer> values = new ArrayList<>(Collections.nCopies(1_000, 42));

    @Benchmark
    public void testBasicForLoop(Blackhole b) {
        for (int i = 0; i < values.size(); ++i) {
            b.consume(values.get(i));
        }
    }

    @Benchmark
    public void testForLoopWithCachedSize(Blackhole b) {
        for (int i = 0, n = values.size(); i < n; ++i) {
            b.consume(values.get(i));
        }
    }

    @Benchmark
    public void testForLoopWithCachedFinalSize(Blackhole b) {
        final int n = values.size();
        for (int i = 0; i < n; ++i) {
            b.consume(values.get(i));
        }
    }

    @Benchmark
    public void testForEach(Blackhole b) {
        for (Integer value : values) {
            b.consume(value);
        }
    }

    @Benchmark
    public void testWhileIerator(Blackhole b) {
        Iterator<Integer> it = values.iterator();
        while (it.hasNext()) {
            b.consume(it.next());
        }
    }

    @Benchmark
    public void testForEachLambda(Blackhole b) {
        values.forEach((value) -> b.consume(value));
    }

    /**
     * <pre>
     * # HOST: Windows 10 - 64bit. Core i5 M520 2.40 GHz. 4 GB RAM
     * $ java -version
     * java version "1.8.0_101"
     * Java(TM) SE Runtime Environment (build 1.8.0_101-b13)
     * Java HotSpot(TM) 64-Bit Server VM (build 25.101-b13, mixed mode)
     *
     * $ mvn clean install
     * $ java -cp target/benchmarks.jar de.foconis.misc.benchmark.LoopBenchmark
     *
     * First run
     * Benchmark                                               Mode  Cnt       Score       Error  Units
     * LoopBenchmark.testBasicForLoop                thrpt    5  144337,290 ±  8449,253  ops/s
     * LoopBenchmark.testForEach                     thrpt    5  131612,294 ± 38038,652  ops/s
     * LoopBenchmark.testForEachLambda               thrpt    5  134350,796 ± 19255,047  ops/s
     * LoopBenchmark.testForLoopWithCachedFinalSize  thrpt    5  148162,065 ± 16794,222  ops/s
     * LoopBenchmark.testForLoopWithCachedSize       thrpt    5  151834,460 ±  5949,224  ops/s
     * LoopBenchmark.testWhileIerator                thrpt    5  138113,430 ± 35968,835  ops/s
     *
     * Second run
     * Benchmark                                      Mode  Cnt       Score       Error  Units
     * LoopBenchmark.testBasicForLoop                thrpt    5  145287,696 ±  3022,676  ops/s
     * LoopBenchmark.testForEach                     thrpt    5  135458,414 ± 11743,290  ops/s
     * LoopBenchmark.testForEachLambda               thrpt    5  136406,003 ± 10512,011  ops/s
     * LoopBenchmark.testForLoopWithCachedFinalSize  thrpt    5  148242,055 ± 34191,601  ops/s
     * LoopBenchmark.testForLoopWithCachedSize       thrpt    5  149890,514 ± 16730,079  ops/s
     * LoopBenchmark.testWhileIerator                thrpt    5  143421,289 ± 22595,459  ops/s
     * </pre>
     *
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        Options opts = new OptionsBuilder()
                .include(LoopBenchmark.class.getSimpleName())
                .mode(Mode.Throughput)
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(5))
                .jvmArgs("-server")
                .forks(1)
                .build();
        new Runner(opts).run();
    }
}