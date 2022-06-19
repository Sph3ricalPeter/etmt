from random import randrange


# Function to check if all subsets are filled or not
def checkSum(sumLeft, k):

    r = True
    for i in range(k):
        if sumLeft[i]:
            r = False

    return r


# Helper function for solving `k` partition problem.
# It returns true if there exist `k` subsets with the given sum
def subsetSum(S, n, sumLeft, A, k):

    # return true if a subset is found
    if checkSum(sumLeft, k):
        return True

    # base case: no items left
    if n < 0:
        return False

    result = False

    # consider current item `S[n]` and explore all possibilities
    # using backtracking
    for i in range(k):
        if not result and (sumLeft[i] - S[n][1]) >= 0:

            # mark the current element subset
            A[n] = i + 1

            # add the current item to the i'th subset
            sumLeft[i] = sumLeft[i] - S[n][1]

            # recur for remaining items
            result = subsetSum(S, n - 1, sumLeft, A, k)

            # backtrack: remove the current item from the i'th subset
            sumLeft[i] = sumLeft[i] + S[n][1]

    # return true if we get a solution
    return result


# Function for solving k–partition problem. It prints the subsets if
# set `S[0…n-1]` can be divided into `k` subsets with equal sum
def partition(S, k):

    # get the total number of items in `S`
    n = len(S)

    # base case
    if n < k:
        print(f"k-partition with k={k} of set S is not possible")
        return partition(S, k - 1)

    # get the sum of all elements in the set
    total = 0
    for t in S:
        total += t[1]

    A = [None] * n

    # create a list of size `k` for each subset and initialize it
    # by their expected sum, i.e., `sum/k`
    sumLeft = [total // k] * k

    # return true if the sum is divisible by `k` and set `S` can
    # be divided into `k` subsets with equal sum
    result = (total % k) == 0 and subsetSum(S, n - 1, sumLeft, A, k)

    if not result:
        print(f"k-partition with k={k} of set S is not possible")
        return partition(S, k - 1)

    # print all k–partitions
    print(f"Found partitions for k={k}:")
    for i in range(k):
        print(f"Partition {i} is", [S[j] for j in range(n) if A[j] == i + 1])


def gen_questions(n):
    questions = []
    for i in range(n):
        questions.append(tuple((f"q{i}", randrange(5, 10))))
    return questions


if __name__ == '__main__':
    # S = gen_questions(50) # set of 10 questions
    # S = [("q1", 3), ("q2", 1), ("q3", 6), ("q4", 2), ("q5", 5), ("q6", 5), ("q7", 5), ("q8", 4)]
    S = [("q1", 2), ("q2", 2), ("q3", 2), ("q4", 5)]
    k = 2  # total of 4 tests

    print(S)
    partition(S, k)
