<template>
  <div>
    <page-header title="订单详情" desc="查看进度并进行操作" />
    <div class="card mt-16" v-if="order">
      <el-steps :active="stepIndex" finish-status="success">
        <el-step title="待确认" />
        <el-step title="待入住" />
        <el-step title="寄养中" />
        <el-step title="已完成" />
      </el-steps>
      <el-divider />
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.id }}</el-descriptions-item>
        <el-descriptions-item label="状态"><status-tag :status="order.status" /></el-descriptions-item>
        <el-descriptions-item label="门店">{{ store.name }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ order.dateRange.join(' 至 ') }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="笼位">{{ order.cageId || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt-16">
        <el-button v-if="canCancel" @click="cancelVisible = true">取消订单</el-button>
        <el-button v-if="canRefund" type="warning" @click="refundVisible = true">申请退款</el-button>
        <el-button v-if="canReschedule" type="primary" @click="rescheduleVisible = true">申请改期</el-button>
        <el-button type="text" @click="$router.push(`/c/boarding/${order.id}`)">查看寄养动态</el-button>
      </div>
    </div>
    <empty-state v-else description="订单不存在" />

    <el-dialog title="取消订单" :visible.sync="cancelVisible" width="420px">
      <el-form ref="cancelForm" :model="cancelForm" :rules="cancelRules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input type="textarea" v-model="cancelForm.reason" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancelVisible = false">返回</el-button>
        <el-button type="primary" @click="submitCancel">确认取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="申请退款" :visible.sync="refundVisible" width="420px">
      <el-form ref="refundForm" :model="refundForm" :rules="refundRules" label-width="80px">
        <el-form-item label="原因" prop="reason">
          <el-input type="textarea" v-model="refundForm.reason" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRefund">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog title="申请改期" :visible.sync="rescheduleVisible" width="420px">
      <el-form ref="resForm" :model="resForm" :rules="resRules" label-width="80px">
        <el-form-item label="新日期" prop="range">
          <el-date-picker v-model="resForm.range" value-format="yyyy-MM-dd" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input type="textarea" v-model="resForm.reason" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rescheduleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReschedule">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { findById, update, requestRefund, requestReschedule } from '@/mock'

export default {
  name: 'OrderDetail',
  components: { PageHeader, EmptyState, StatusTag },
  data() {
    return {
      order: null,
      store: {},
      cancelVisible: false,
      refundVisible: false,
      rescheduleVisible: false,
      cancelForm: { reason: '' },
      refundForm: { reason: '' },
      resForm: { range: [], reason: '' },
      cancelRules: { reason: [{ required: true, message: '请输入原因', trigger: 'blur' }] },
      refundRules: { reason: [{ required: true, message: '请输入原因', trigger: 'blur' }] },
      resRules: {
        range: [{ required: true, message: '请选择日期', trigger: 'change' }],
        reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
      }
    }
  },
  computed: {
    stepIndex() {
      const map = { pending_confirm: 0, pending_checkin: 1, boarding: 2, completed: 3 }
      return map[this.order.status] || 0
    },
    canCancel() {
      return ['draft', 'pending_confirm'].includes(this.order.status)
    },
    canRefund() {
      return ['pending_checkin', 'boarding', 'completed'].includes(this.order.status)
    },
    canReschedule() {
      return ['pending_checkin'].includes(this.order.status)
    }
  },
  created() {
    const id = this.$route.params.id
    this.order = findById('orders', id)
    if (this.order) {
      this.store = findById('stores', this.order.storeId) || {}
    }
  },
  methods: {
    submitCancel() {
      this.$refs.cancelForm.validate(valid => {
        if (!valid) return
        update('orders', this.order.id, { status: 'cancelled', cancelReason: this.cancelForm.reason })
        this.order.status = 'cancelled'
        this.cancelVisible = false
        this.$message.success('订单已取消')
      })
    },
    submitRefund() {
      this.$refs.refundForm.validate(valid => {
        if (!valid) return
        requestRefund(this.order.id, this.refundForm.reason)
        this.order.status = 'refund_pending'
        this.refundVisible = false
        this.$message.success('退款申请已提交')
      })
    },
    submitReschedule() {
      this.$refs.resForm.validate(valid => {
        if (!valid) return
        requestReschedule(this.order.id, this.resForm.range, this.resForm.reason)
        this.order.status = 'reschedule_pending'
        this.rescheduleVisible = false
        this.$message.success('改期申请已提交')
      })
    }
  }
}
</script>
