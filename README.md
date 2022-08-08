# AGO-BE API

**Hostname**: ec2-54-180-159-230.ap-northeast-2.compute.amazonaws.com <br/>
**API Docs**: http://ec2-54-180-159-230.ap-northeast-2.compute.amazonaws.com/api/docs/swagger-ui
<br/>
## API Docs에 포함되어 있지 않은 API

컨트롤러 앞단의 Filter와 Interceptor로 처리되는 API들로 swagger에서 인식X <br/>

### Login API <hr/>
POST  /api/auth/login <br/>
Request body: { "email": "string", "password": "string" } <br/><br/>
로그인 성공시 Response Header의 Authentication 값으로 전달되는 JWT Token을 가지고 API 통신 진행
<br/>
### Docker Container 생성 API <hr/>
POST  /api/es/{container_name} <br/>
ex) POST  ec2-54-180-159-230.ap-northeast-2.compute.amazonaws.com/api/es/haneumjoo
<br/>
### Elasticsearch API <hr/>
[GET, POST, PUT, DELETE]  /api/es/{container_name}/{elasticsearch_api} <br/>
ex) GET  ec2-54-180-159-230.ap-northeast-2.compute.amazonaws.com/api/es/haneumjoo/my_index/_settings
