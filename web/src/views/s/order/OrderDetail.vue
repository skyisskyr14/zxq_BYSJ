<template>
  <div>
    <page-header title="订单详情" desc="处理退款与改期建议" />
    <div class="card mt-16" v-if="order">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.id }}</el-descriptions-item>
        <el-descriptions-item label="状态"><status-tag :status="order.status" /></el-descriptions-item>
        <el-descriptions-item label="门店">{{ store.name }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ order.dateRange.join(' 至 ') }}</el-descriptions-item>
        <el-descriptions-item label="金额">￥{{ order.amount }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="card mt-16" v-if="refund">
      <div class="section-title">退款申请</div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="原因">{{ refund.reason }}</el-descriptions-item>
        <el-descriptions-item label="状态"><status-tag :status="refund.status" /></el-descriptions-item>
        <el-descriptions-item label="商家意见">{{ refund.merchantOpinion || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt-12">
        <el-button type="primary" :disabled="refund.merchantOpinion" @click="openOpinion('refund')">提交处理意见</el-button>
      </div>
    </div>

    <div class="card mt-16" v-if="reschedule">
      <div class="section-title">改期申请</div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="原因">{{ reschedule.reason }}</el-descriptions-item>
        <el-descriptions-item label="新日期">{{ reschedule.newRange.join(' 至 ') }}</el-descriptions-item>
        <el-descriptions-item label="状态"><status-tag :status="reschedule.status" /></el-descriptions-item>
        <el-descriptions-item label="商家意见">{{ reschedule.merchantOpinion || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt-12">
        <el-button type="primary" :disabled="reschedule.merchantOpinion" @click="openOpinion('reschedule')">提交处理意见</el-button>
      </div>
    </div>

    <el-dialog title="处理意见" :visible.sync="dialogVisible" width="420px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="意见" prop="opinion">
          <el-input type="textarea" v-model="form.opinion" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOpinion">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { findById, list, update } from '@/mock'

export default {
  name: 'SOrderDetail',
  components: { PageHeader, StatusTag },
  data() {
    return {
      order: null,
      store: {},
      refund: null,
      reschedule: null,
      dialogVisible: false,
      dialogType: '',
      form: { opinion: '' },
      rules: { opinion: [{ required: true, message: '请输入意见', trigger: 'blur' }] }
    }
  },
  created() {
    const id = this.$route.params.id
    this.order = findById('orders', id)
    if (this.order) {
      this.store = findById('stores', this.order.storeId) || {}
    }
    this.refund = list('refunds').find(r => String(r.orderId) === String(id)) || null
    this.reschedule = list('reschedules').find(r => String(r.orderId) === String(id)) || null
  },
  methods: {
    openOpinion(type) {
      this.dialogType = type
      this.form = { opinion: '' }
      this.dialogVisible = true
    },
    submitOpinion() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.dialogType === 'refund' && this.refund) {
          update('refunds', this.refund.id, { merchantOpinion: this.form.opinion })
          this.refund.merchantOpinion = this.form.opinion
        }
        if (this.dialogType === 'reschedule' && this.reschedule) {
          update('reschedules', this.reschedule.id, { merchantOpinion: this.form.opinion })
          this.reschedule.merchantOpinion = this.form.opinion
        }
        this.dialogVisible = false
        this.$message.success('已提交')
      })
    }
  }
}
</script>

