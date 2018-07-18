FROM python:3

WORKDIR /usr/src/app

COPY . .

RUN pip3 install -r requirements.txt

USER nobody:nogroup

ENTRYPOINT [ "python3", "integration_tests.py", "-i", "configuration.json" ]
