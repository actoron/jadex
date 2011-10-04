set grid

plot "deficient.dat" using 1:2 w lp title "deficient_by_break"
replot "deficient.dat" using 1:3 w lp title "deficient_by_change"
replot "deficient.dat" using 1:4 w lp title "not_deficient"
      
pause -1 
                                     
set output "deficient.png"
set terminal png                                              
replot