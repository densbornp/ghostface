FROM ubuntu
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt update && apt install -y python3 python3-pip nodejs npm
RUN mkdir ghostface
COPY . /ghostface
WORKDIR /ghostface
RUN pip3 install --no-cache-dir -r requirements.txt
WORKDIR /ghostface/frontend
RUN npm install
WORKDIR /ghostface/backend
ENTRYPOINT ["./mvnw", "spring-boot:run"]
