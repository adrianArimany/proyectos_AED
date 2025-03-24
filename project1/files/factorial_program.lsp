(defun factorial (n)
    "Some Comment: 
    Returns 1 for N = 0, and N * factorial(N-1) for N > 0"
    (if (<= n 1)
        1
        (* n (factorial (- n 1)))))

