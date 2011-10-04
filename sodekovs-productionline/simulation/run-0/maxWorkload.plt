set grid

plot "maxWorkload.dat" using 1:2 w lp title "maxWorkload"

pause -1

set output "maxWorkload.png"
set terminal png                                              
replot