set grid

plot "main.dat" using 1:2 w lp title "running(bussy)"
replot "main.dat" using 1:3 w lp title "running(idle)"
replot "main.dat" using 1:4 w lp title "waiting_for_reconf"

pause -1

set output "main.png"
set terminal png                                              
replot