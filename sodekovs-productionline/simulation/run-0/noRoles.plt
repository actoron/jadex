set grid

plot "noRoles.dat" using 1:2 w lp title "noRoles"

pause -1

set output "noRoles.png"
set terminal png                                              
replot