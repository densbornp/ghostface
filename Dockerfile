FROM ubuntu
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apt update && apt install -y python3 python3-pip nodejs npm
RUN mkdir ghostface
COPY . /ghostface
WORKDIR /ghostface
RUN rm -R .git
RUN pip3 install --no-cache-dir -r requirements.txt
RUN npm install
RUN chown -R www-data:www-data public
RUN chmod -R 766 public
RUN mkdir public/uploads
RUN chmod -R 666 public/uploads
ENTRYPOINT ["node", "app.js"]
