//
// Created by charlie on 1/9/21.
#include <stdarg.h>
#include <stdlib.h>
#include <check.h>
#include <fcntl.h>
#include <stdio.h>
#include <elf.h>
#include <libelf.h>
#include <string.h>
#include <stdarg.h>
#include <stdlib.h>
#include <fcntl.h>
#include <stdio.h>
#include <elf.h>
#include <libelf.h>
#include <stdlib.h>
#include <string.h>

#define DEFINE_TEST(__function_name) \
static const TTest * __function_name##FAKE; \
void __function_name() {\
int number_failed;\
struct Suite *s;\
SRunner *sr;\
\
s = make_suite(#__function_name, __function_name##FAKE, NULL);\
sr = srunner_create(s);\
\
srunner_run_all(sr, CK_NORMAL);\
number_failed = srunner_ntests_failed(sr);\
srunner_free(sr);\
exit(number_failed);\
}                                    \
START_TEST(__function_name##FAKE)

Suite *make_suite(char *name, ...) {
    va_list ap;
    va_start(ap, name);

    TCase *tc_core = tcase_create("Core");

    void *ptr = va_arg(ap, void *);
    while (ptr != NULL) {
        tcase_add_test(tc_core, ptr);
        ptr = va_arg(ap, void *);
    }

    va_end(ap);

    Suite *s = suite_create(name);
    suite_add_tcase(s, tc_core);
    return s;
}