FROM python:3
COPY ./server /
RUN apt-get update && apt-get upgrade -y
RUN apt-get install python3 -y
RUN apt-get install python3-pip -y
RUN pip3 install numpy pandas scikit-learn==1.0.2
EXPOSE 6543
CMD [ "python", "./server.py" ]