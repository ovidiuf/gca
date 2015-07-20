#TODO

#Next

* test version and release date

* gca ./gc.log.04-Dec-13-020427 export -c cms-concurrent-mark-start

    [error]: unknown export option: cms-concurrent-mark-start
    
    2014-08-14T01:12:28.621-0700: 27036.837: [GC2014-08-14T01:12:28.622-0700: 27036.837: [ParNew (promotion failed): 471872K->471872K(471872K), 0.3931530 secs]

    2014-08-14T01:12:29.015-0700: 27037.231: [CMS2014-08-14T01:12:29.867-0700: 27038.083: [CMS-concurrent-preclean: 4.167/17.484 secs] [Times: user=21.55 sys=2.82, real=17.48 secs]
    (concurrent mode failure): 3667441K->779130K(3670016K), 6.2096720 secs] 4045730K->779130K(4141888K), [CMS Perm : 93567K->92821K(131072K)] icms_dc=100 , 6.6030840 secs] [Times: user=2.31 sys=0.23, real=6.61 secs]


# Backlog

* I should be able to export all fields by default. If a field does not apply, it should generate ‘’ in the CSV file (04/28/15).

* I should not throw any exception and not stop parsing, just log a warning and go on. This way I can extract some info from a file instead of stopping (04/28/15).

* Standardize the automatic maven dependency pull from pom.xml into the final artifact (release.sh) (04/28/15)

* Add garbage_collection.log-08-28-2013_06-40-47 to src\test\resources\collected and write a test that passes around it.

* Clarify ParserException vs Exception in GCEventParser.parse(..)

* What is the difference between the first and second duration in the example. When figuring out go to TODO_swev
1.985: [GC 1.984: [ParNew: 136320K->6357K(153344K), 0.0083580 secs] 136320K->6357K(4177280K), 0.0085020 secs] [Times: user=0.05 sys=0.01, real=0.01 secs]

* Implement expression: heap-after - og-after

* Should be able to calculate time from previous collection (and time from previous collection of the same type)

* Define strategy on log.info() vs System.out.print. In a command line environment I need the utility to be "quiet" - not generate any  undesired output.

* Re-implement it in such a way to make sure that after a pass, I "understand" every bit of that gc log file, and if there are bits I don't  understand, I throw an exception. This should be the default behavior. Then, I should have the option to turn the --strict behavior on,  and extract as much as possible, with warnings as comments embedded in the output file.

* The gc log analysis code should be designed in such a way that it runs from a command line interface and from a server process.

* Parallel GC - update wiki and introduce more formal support for it.

* Currently I am building the distributable ZIP in a separated build.xml file - integrate this with Maven "the right way" - one module  that builds the JAR and one module that builds the ZIP.

* Remove all remnant gc analysis classes classes from Universus, keep Universus clean from gc analysis.
