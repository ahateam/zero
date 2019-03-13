<template>
  <div id="app">

    <router-view/>
  </div>
</template>

<script>
  export default {
      name:'app',
      created(){
        this.$router.push('/test')
      }
  }
</script>
<style lang="scss">

</style>
