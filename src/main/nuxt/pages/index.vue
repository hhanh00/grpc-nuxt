<template>
  <div class="container">
    <div> {{ greeting }} </div>
  </div>
</template>

<script>
const {HelloRequest, HelloReply} = require('~/components/helloworld_pb.js')
const {GreeterClient} = require('~/components/helloworld_grpc_web_pb.js')


export default {
  mounted() {
    const client = new GreeterClient('http://localhost:8001')
    const request = new HelloRequest()
    request.setName("John")
    const r = client.sayHello(request, {}, (err, resp) => {
      this.greeting = resp.getMessage()
    })
  },

  data() {
    return {
      greeting: ''
    }
  }
}
</script>

<style>
.container {
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}
</style>
