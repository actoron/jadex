# Builds an image containing pandoc and latex to generate PDFs from Markdown.

FROM jagregory/pandoc:latest

RUN apt-get update -y \
  && apt-get install -y --no-install-recommends \
    lmodern \
    texlive-generic-recommended \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*