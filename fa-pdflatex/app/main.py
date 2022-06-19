# app/main.py

import os
import pathlib
from subprocess import PIPE, Popen
 
from fastapi import FastAPI, Request, Response
from pydantic import FilePath

app = FastAPI(title="pdflatex FastAPI")

SRC_PATH = "/tmp/pdftex"
OUT_PATH = SRC_PATH + "/out"
FNAME = "main"


@app.get("/")
async def read_root():
    return "pdflatex FastAPI is ready"


@app.post("/upload")
async def upload(request: Request):
    data: bytes = await request.body()

    # create out folder in case it doesn't exist
    pathlib.Path(OUT_PATH).mkdir(parents=True, exist_ok=True)

    with open(f"{SRC_PATH}/{FNAME}.tex", "wb+") as src_file:
        src_file.write(data)

    cmd = f"pdflatex -interaction=nonstopmode -output-directory {OUT_PATH} {SRC_PATH}/{FNAME}.tex"

    print(f"running: {cmd}")
    p = Popen(cmd, shell=True, stdout=PIPE).stdout.read()

    with open(f"{SRC_PATH}/out/{FNAME}.pdf", "rb") as out_file:
        out_data = out_file.read()

    return Response(out_data)


@app.on_event("startup")
async def startup():
    # setup
    pass


@app.on_event("shutdown")
async def shutdown():
    # shutdown
    pass
