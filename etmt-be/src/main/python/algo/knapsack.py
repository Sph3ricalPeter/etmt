def uniformKnapsack(items, W):
    import random

    def subProcedure(remaining, new_W):
        # base case
        if remaining == [] or min(i[0] for i in remaining) > new_W:
            return []

        # sample random item
        random_item = random.choice(remaining)

        # split input into larger and smaller subsets based on the item
        smaller = [i for i in remaining if i < random_item]
        not_smaller = [i for i in remaining if random_item <= i]

        # compute the total cost of all smaller items
        smaller_cost = sum(i[0] for i in smaller)

        if smaller_cost <= new_W:
            # include all smaller elements and recurse on the rest with a smaller W
            return smaller + subProcedure(not_smaller, new_W - smaller_cost)
        else:
            # recurse on the smaller subset with the same W
            return subProcedure(smaller, new_W)

    # convert a list of items (a, b, c) to  a list of the form ((a, 0), (b, 1), (c, 2)) for
    # consistent tie breaking in comparison operations
    augmented_items = [(j, i) for (i, j) in enumerate(items)]

    # compute the solution over the augmented items
    solution = subProcedure(augmented_items, W)

    # return the un-augmented items from the solution
    return [i[0] for i in solution]
