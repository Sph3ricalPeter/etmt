from random import randrange

# generates all possible combinations of questions for a given sum of points
def subset_sum_iter(array, target):
    array = sorted(array, key=lambda tup: tup[1])

    last_index = {0: [-1]}
    for i in range(len(array)):
        for s in list(last_index.keys()):
            new_s = s + array[i][1]
            if 0 < (new_s - target):
                pass  # Cannot lead to target
            elif new_s in last_index:
                last_index[new_s].append(i)
            else:
                last_index[new_s] = [i]

    # Now yield up the answers.
    def recur(new_target, max_i):
        for i in last_index[new_target]:
            if i == -1:
                yield []  # Empty sum.
            elif max_i <= i:
                break  # Not our solution.
            else:
                for answer in recur(new_target - array[i][1], i):
                    answer.append(array[i])
                    yield answer

    for answer in recur(target, len(array)):
        yield answer

def gen_questions(n):
    questions = []
    for i in range(n): 
        questions.append(tuple((f"q{i}", randrange(5, 20))))
    return questions

if __name__ == "__main__":
    questions = gen_questions(20)
    print(questions)
    for answer in subset_sum_iter(questions, 50):
        print(answer)
