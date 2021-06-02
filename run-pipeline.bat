@rem Run Gitlab pipeline job on windows, e.g.: 'run-pipeline.bat build'

@rem Convert script directory to unix form (e.g. '/C/mydata/myproject').
@set MYPATH=%~dp0
@set MYPATH=%MYPATH:~0,-1%
@call set "MYPATH=/%MYPATH:\=/%"
@call set "MYPATH=%MYPATH::=%"

@rem fix broken mount in inner docker containerfor gitlab runner in docker on windows
@rem https://gitlab.com/gitlab-org/gitlab-runner/-/issues/4574#note_302916410
@set RUNNER_ARGS=--docker-volumes %MYPATH%:/jadex_tmp --docker-privileged --pre-clone-script "umount /jadex1; mv jadex1 jadex1_copy; cp -r jadex_tmp jadex1"

docker run --rm -it -v %MYPATH%:/jadex1:ro -v /var/run/docker.sock:/var/run/docker.sock -w /jadex1 gitlab/gitlab-runner:ubuntu-v12.10.2 exec docker %1 %RUNNER_ARGS%

@pause
