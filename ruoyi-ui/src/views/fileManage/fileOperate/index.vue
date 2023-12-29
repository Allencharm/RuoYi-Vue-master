<template>
  <div class="app-container" style="height:1000px; width:100%;margin:0;border:0;">

    <iframe :src="flowSrc" style="height:100%; width:100%;margin:0;border:0;"> </iframe>

  </div>
</template>

<script>
import { list, forceLogout } from "@/api/monitor/online";

export default {
  name: "Online",
  data() {
    return {

      flowSrc:null,

      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      pageNum: 1,
      pageSize: 10,
      // 查询参数
      queryParams: {
        ipaddr: undefined,
        userName: undefined
      }
    };
  },
  created() {
    // this.getList();
    //this.flowSrc = 'http://127.0.0.1:8080/rtb/ftp/file?ftpId=b6a1071d87554fa6adfa364365f6c6b6&ftpType=3';  //本地
    this.flowSrc = 'http://127.0.0.1:8080/file/view';  //本地
    //this.flowSrc = 'http://10.190.107.146:8080/rtb/ftp/file?ftpId=fcc7639c53b84623b324321797367462&ftpType=3';  //本地
  },
  methods: {
    /** 查询登录日志列表 */
    getList() {
      this.loading = true;
      list(this.queryParams).then(response => {
        this.list = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 强退按钮操作 */
    handleForceLogout(row) {
      this.$modal.confirm('是否确认强退名称为"' + row.userName + '"的用户？').then(function() {
        return forceLogout(row.tokenId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("强退成功");
      }).catch(() => {});
    }
  }
};
</script>
