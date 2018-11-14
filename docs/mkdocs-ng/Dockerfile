# Builds an image containing pandoc and latex to generate PDFs from Markdown.

FROM haskell:8.0

MAINTAINER James Gregory <james@jagregory.com>

# install latex packages
RUN apt-get update -y \
  && apt-get install -y -o Acquire::Retries=10 --no-install-recommends \
    texlive-latex-base \
    texlive-xetex latex-xcolor \
    texlive-math-extra \
    texlive-latex-extra \
    texlive-fonts-extra \
    texlive-bibtex-extra \
    fontconfig \
    lmodern

# will ease up the update process
# updating this env variable will trigger the automatic build of the Docker image
ENV PANDOC_VERSION "1.19.2.1"

RUN useradd -ms /bin/bash pandoc

USER pandoc

# install pandoc
RUN cabal update && cabal install pandoc-${PANDOC_VERSION}

USER root

RUN apt-get update -y \
  && apt-get install -y --no-install-recommends \
    lmodern \
    texlive-generic-recommended \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

USER pandoc

WORKDIR /source

ENTRYPOINT ["/home/pandoc/.cabal/bin/pandoc"]

CMD ["--help"]
