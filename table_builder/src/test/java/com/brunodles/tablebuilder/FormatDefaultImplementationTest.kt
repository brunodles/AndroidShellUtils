package com.brunodles.tablebuilder

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class FormatDefaultImplementationTest {

    @ParameterizedTest
    @MethodSource("method")
    fun shouldBuildTheExpectedTable(format: Format, expectedTable: String) {
        val result = TableBuilder(format)
            .columns {
                add("col1111111111", ColumnDirection.left)
                add("col2", ColumnDirection.center)
                add("col3333333333", ColumnDirection.right)
            }
            .newRow {
                add("r1")
                add("r2222222222222")
                add("r3")
            }
            .newRow {
                add("r1")
                add("r2")
                add("r3")
            }
            .newFooter{
                add("f1")
                add("f2")
                add("f3")
            }
            .build()

        assertEquals(expectedTable, result)
    }

    companion object {

        private val SIMPLE_TABLE = """
            col1111111111 |      col2      | col3333333333
            ------------- | -------------- | -------------
            r1            | r2222222222222 |            r3
            r1            |       r2       |            r3
            ------------- | -------------- | -------------
            f1            |       f2       |            f3
            
        """.trimIndent()
        private val TERMINAL_TABLE = """
            col1111111111 |      col2      | col3333333333
            ------------- + -------------- + -------------
            r1            | r2222222222222 |            r3
            r1            |       r2       |            r3
            ============= + ============== + =============
            f1            |       f2       |            f3
            
        """.trimIndent()

        private val MARKD0WN_TABLE = """
            | col1111111111 |      col2      | col3333333333 |
            | :------------ | :------------: | ------------: |
            | r1            | r2222222222222 |            r3 |
            | r1            |       r2       |            r3 |
            | ============= | ============== | ============= |
            | f1            |       f2       |            f3 |

        """.trimIndent()

        private val JIRA_TABLE = """
            ||col1111111111||col2||col3333333333||
            |r1|r2222222222222|r3|
            |r1|r2|r3|
            |f1|f2|f3|

        """.trimIndent()

        private val JIRA_SPACED_WITH_FOOTER_DIVIDER_TABLE = """
            || col1111111111 ||      col2      || col3333333333 ||
            |  r1            |  r2222222222222 |             r3  |
            |  r1            |        r2       |             r3  |
            |  ------------- |  -------------- |  -------------  |
            |  f1            |        f2       |             f3  |

        """.trimIndent()

        private val JIRA_SPACED_WITHOUT_FOOTER_DIVIDER_TABLE = """
            || col1111111111 ||      col2      || col3333333333 ||
            |  r1            |  r2222222222222 |             r3  |
            |  r1            |        r2       |             r3  |
            |  f1            |        f2       |             f3  |

        """.trimIndent()

        private val CSV_TABLE = """
            col1111111111,col2,col3333333333
            r1,r2222222222222,r3
            r1,r2,r3
            f1,f2,f3

        """.trimIndent()

        private val CSV_WITH_FOOTER_DIVIDER_TABLE = """
            col1111111111,col2,col3333333333
            r1,r2222222222222,r3
            r1,r2,r3
            -,-,-
            f1,f2,f3

        """.trimIndent()

        private val TSV_TABLE = """
            col1111111111	col2	col3333333333
            r1	r2222222222222	r3
            r1	r2	r3
            f1	f2	f3

        """.trimIndent()

        @JvmStatic
        fun method(): Stream<Arguments> = Stream.of(
            Arguments.of(FormatDefault.simple, SIMPLE_TABLE),
            Arguments.of(FormatDefaultImplementation.Simple, SIMPLE_TABLE),
            Arguments.of(FormatDefaultImplementation.Terminal, TERMINAL_TABLE),
            Arguments.of(FormatDefault.markdown, MARKD0WN_TABLE),
            Arguments.of(FormatDefaultImplementation.Markdown, MARKD0WN_TABLE),
            Arguments.of(FormatDefault.jira, JIRA_SPACED_WITH_FOOTER_DIVIDER_TABLE),
            Arguments.of(FormatDefaultImplementation.JiraSpaced, JIRA_SPACED_WITH_FOOTER_DIVIDER_TABLE),
            Arguments.of(FormatDefaultImplementation.JiraSpaced2, JIRA_SPACED_WITH_FOOTER_DIVIDER_TABLE),
            Arguments.of(FormatDefaultImplementation.JiraSpacedWithoutFooterDivider, JIRA_SPACED_WITHOUT_FOOTER_DIVIDER_TABLE),
            Arguments.of(FormatDefaultImplementation.Jira, JIRA_TABLE),
            Arguments.of(FormatDefault.csv, CSV_WITH_FOOTER_DIVIDER_TABLE),
            Arguments.of(FormatDefaultImplementation.CsvWithFooterDivider, CSV_WITH_FOOTER_DIVIDER_TABLE),
            Arguments.of(FormatDefaultImplementation.Csv, CSV_TABLE),
            Arguments.of(FormatDefaultImplementation.Tsv, TSV_TABLE),
        )
    }

}