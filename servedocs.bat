@REM Launch local mkdocs at http://localhost:8000/
@REM requires docker for windows: https://docs.docker.com/docker-for-windows/install/
docker run -it --rm -v %cd%\docs\mkdocs-ng:/docs -p 8000:8000 melopt/mkdocs serve
