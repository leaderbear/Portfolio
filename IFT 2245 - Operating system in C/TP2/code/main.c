#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <pthread.h>
#include "blocking_q.h"
#include "main.h"

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"

#define TODO printf("TODO");

#define TASK_A_T (5 * 1000)
#define TASK_B_T (10 * 1000)
#define TASK_C_T (15 * 1000)
#define TASK_D_T (20 * 1000)

#define PROCESSOR_COUNT 4

#define POISON_PILL 'K'

/**
 * Code executed by task A
 */
long task_a() {
    printf("Task A starting...\n");
    sleep(5);
    printf("Task A ending...\n");
    return TASK_A_T;
}

/**
 * Code executed by task B
 */
long task_b() {
    printf("Task B starting...\n");
    sleep(10);
    printf("Task B ending...\n");
    return TASK_B_T;
}

/**
 * Code executed by task C
 */
long task_c() {
    printf("Task C starting...\n");
    sleep(15);
    printf("Task C ending...\n");
    return TASK_C_T;
}

/**
 * Code executed by task D
 */
long task_d() {
    printf("Task D starting...\n");
    sleep(20);
    printf("Task D ending...\n");
    return TASK_D_T;
}

/**
 * Initialises a processor structure. This can fail if there is no
 * memory for a tasks list, it's initialisation fails or the mutex
 * cannot be created.
 * @param id the ID of the processor
 * @param p the processor
 * @return if the initialization was successful
 */
bool processor_init(int id, processor *p) {
    // TODO
    p->id = id;
    p->sched_t = 0;
    p->work_t = 0;
    p->real_t = 0;
    p->wait_t = 0;

    p->tasks = malloc(sizeof(blocking_q));
    if (p->tasks == NULL) {
        return false;
    }

    if (!blocking_q_init(p->tasks)) {
        blocking_q_destroy(p->tasks);
        free(p->tasks);
        return false;
    }

    if (pthread_mutex_init(&p->lock, NULL) != 0) {
        blocking_q_destroy(p->tasks);
        free(p->tasks);
        return false;
    }

    return true;
}

/**
 * Destroy a processor structure
 * @param p ptr to the structure
 */
void processor_destroy(processor *p) {
    // TODO
    pthread_mutex_lock(&p->lock);

    p->sched_t = 0;
    p->work_t = 0;
    p->real_t = 0;
    p->wait_t = 0;

    if(p->tasks) {
        blocking_q_destroy(p->tasks);
        free(p->tasks);
    }

    pthread_mutex_unlock(&p->lock);
    pthread_mutex_destroy(&p->lock);
}

void *processor_run(void *v_self) {
    //TODO
    processor *self = (processor *) v_self;
    task_ptr task = NULL;
    while (true) {
        task = blocking_q_get(self->tasks);
        if (POISON_PILL == task->type) {
            break;
        }
        long start = time(NULL);
        switch (task->type) {
            case 'A':
                self->work_t += task_a()/1000;
                break;
            case 'B':
                self->work_t += task_b()/1000;
                break;
            case 'C':
                self->work_t += task_c()/1000;
                break;
            case 'D':
                self->work_t += task_d()/1000;
                break;
        }
        long end = time(NULL);
        self->real_t += end - start;
        self->wait_t += end - task->end;
    }
    return NULL;
}


void *scheduler(void *v_sched_data) {
    sched_data *data = (sched_data *) v_sched_data;
    blocking_q *q = data->sched_q;
    processor *p = data->processors;

    task_ptr poison = NULL;

    while (!poison) {
        task_ptr t = blocking_q_get(q);
        printf("Received t %c\n", t->type);

        /// ------------------------------------------------------------------
        ///           EXERCICE 2.4 DANS LE BLOC LEXICAL SUIVANT
        /// ------------------------------------------------------------------
        {
            static int current_processor = 0;
            processor *min_load_processor = &p[current_processor];
            t->end = time(NULL);
            blocking_q_put(min_load_processor->tasks, t);
            current_processor = (current_processor + 1) % PROCESSOR_COUNT;
        }
        /// ------------------------------------------------------------------
        ///                NE PAS TOUCHER APRÈS CETTE LIGNE
        /// ------------------------------------------------------------------


        if (POISON_PILL == t->type) {
            poison = t;
        }
    }

    // Stop the processors
    for (int i = 0; i < PROCESSOR_COUNT; ++i) {
        processor *proc = data->processors + i;
        // kill all processors
        blocking_q_put(proc->tasks, poison);
    }

    return NULL;
}


// ATTENTION! TOUT CE QUI EST ENTRE LES BALISES ༽つ۞﹏۞༼つ SERA ENLEVÉ! N'AJOUTEZ PAS D'AUTRES ༽つ۞﹏۞༼つ
// ༽つ۞﹏۞༼つ
/**
 * Entry point to your homework. DO NOT, UNLESS TOLD BY AN INSTRUCTOR, CHANGE ANY CODE IN THIS
 * FUNCTION. DOING SO WILL GIVE YOU THE GRADE 0.
 * @return exit code
 */
int main(int argc, char **argv) {
    /*
     *  Example of an argument string you can use for test/debug:
     *  ABCD5AB5CD5A9B9CDABCD
     *
     *  Letters are tasks
     *  Numbers are delays
     *
     */
    if (argc < 2) {
        printf("Missing / Wrong arguments.\n");
        return EXIT_FAILURE;
    }

    // Start threads
    blocking_q *sched_q = malloc(sizeof(blocking_q));

    if (NULL == sched_q || !blocking_q_init(sched_q)) {
        return EXIT_FAILURE;
    }

    pthread_t sched_thread;
    pthread_t processor_threads[PROCESSOR_COUNT];
    processor processors[PROCESSOR_COUNT];

    sched_data data;
    data.sched_q = sched_q;
    data.processors = processors;

    if (0 != pthread_create(&sched_thread, NULL, scheduler, (void *) &data)) {
        return EXIT_FAILURE;
    }

    long start = time(NULL);
    for (int i = 0; i < PROCESSOR_COUNT; ++i) {

        if (!processor_init(i, processors + i)) {
            return EXIT_FAILURE;
        }

        if (0 != pthread_create(processor_threads + i,
                                NULL,
                                processor_run,
                                (void *) (processors + i))) {

            return EXIT_FAILURE;
        }
    }

    char *tasks_and_times = argv[1];

    // Fill the task queue
    unsigned long task_c = strlen(tasks_and_times);

    size_t total_sz = sizeof(task) * task_c;
    task_ptr tasks = (task_ptr) malloc(total_sz);
    memset(tasks, 0, total_sz);

    for (unsigned long i = 0; i < task_c; ++i) {
        char task_type = tasks_and_times[i];

        switch (task_type) {
            case 'A':
            case 'B':
            case 'C':
            case 'D': {
                task_ptr t = tasks + i;
                t->type = task_type;
                t->start = t->end = 0;
                blocking_q_put(sched_q, t);
                break;
            }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                sleep(task_type - '0');
                break;
            default:
                break;
        }
    }

    task_ptr poison_pill_task = malloc(sizeof(task));
    poison_pill_task->type = POISON_PILL;

    blocking_q_put(sched_q, poison_pill_task);

    pthread_join(sched_thread, NULL);

    blocking_q_destroy(sched_q);

    printf("\n\n");

    for (int i = 0; i < PROCESSOR_COUNT; ++i) {
        pthread_join(processor_threads[i], NULL);

        processor *p = processors + i;
        printf("Processor %d: Real T: %ld Work T: %ld Wait T: %ld\n",
               i,
               p->real_t,
               p->work_t,
               p->wait_t);
    }

    long end = time(NULL);
    long elapsed = end - start;

    printf("Elapsed: %ld\n", elapsed);

    free(poison_pill_task);

    for (int i = 0; i < PROCESSOR_COUNT; ++i) {
        processor *p = processors + i;
        processor_destroy(p);
    }

    free(tasks);
    free(sched_q);

    return EXIT_SUCCESS;
}
// ༽つ۞﹏۞༼つ
