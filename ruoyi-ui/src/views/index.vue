<template>

  <h1 id="wenzi">欢迎使用信用卡文件内部管理平台</h1>

</template>

<script>
console.log(process.env.VUE_APP_TITLE);
console.log(process.env);

export default {
  name: "Index",
  data() {
    return {

    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    }
  }
};
</script>

<style scoped lang="scss">
#wenzi{
  text-align: center;
  vertical-align: middle;
  position: absolute;
  top: 40%;
  left:30%;
}
</style>

