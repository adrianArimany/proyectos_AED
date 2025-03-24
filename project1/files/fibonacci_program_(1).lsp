(DEFUN fibonacci  (N) 
    (IF (EQUAL N 0) 0 
    (IF (EQUAL N 1) 1 
        (+ (fibonacci (- N 1)) (fibonacci (- N 2)) ))))