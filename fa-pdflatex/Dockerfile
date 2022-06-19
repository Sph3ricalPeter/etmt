# Dockerfile

# pull the official docker image
FROM ubuntu:18.04

# update & upgrade ubuntu
RUN apt update && apt upgrade -y && apt-get install -y python3.10 && apt-get install -y python3-pip

# install texlive
RUN apt-get install -y --no-install-recommends texlive-latex-recommended texlive-fonts-recommended && \
    # apt-get install -y --no-install-recommends texlive-latex-extra texlive-fonts-extra texlive-lang-all && \
    rm -rf /var/lib/apt/lists/*

# set work directory
WORKDIR /app

# install dependencies
COPY requirements.txt .
RUN /usr/bin/python3 -m pip install -r requirements.txt

RUN mkdir /tmp/pdftex && mkdir /tmp/pdftex/out

# todo: rm reload option for release
CMD ["uvicorn", "app.main:app", "--reload", "--host=0.0.0.0"]
