<template>
  <div>
    <page-header title="创建预约" desc="选择门店、宠物与服务并生成订单" />
    <div class="card mt-16">
      <el-steps :active="step" finish-status="success">
        <el-step title="选择门店" />
        <el-step title="选择宠物" />
        <el-step title="日期与服务" />
        <el-step title="确认提交" />
      </el-steps>
    </div>

    <div class="card mt-16" v-if="step === 0">
      <div class="section-title">选择门店</div>
      <div class="store-grid">
        <div v-for="store in stores" :key="store.id" class="store-item" :class="{ active: form.storeId === store.id }">
          <img :src="store.cover" alt="cover" />
          <div class="info">
            <div class="name">{{ store.name }}</div>
            <div class="text-muted">{{ store.city }} · 评分 {{ store.rating }}</div>
          </div>
          <el-button size="mini" type="primary" @click="form.storeId = store.id">选择</el-button>
        </div>
      </div>
    </div>

    <div class="card mt-16" v-if="step === 1">
      <div class="section-title">选择宠物</div>
      <el-radio-group v-model="form.petId">
        <el-radio v-for="pet in pets" :key="pet.id" :label="pet.id">{{ pet.name }} ({{ pet.type }})</el-radio>
      </el-radio-group>
      <el-button class="mt-12" type="text" @click="$router.push('/c/pet/list')">去管理宠物</el-button>
    </div>

    <div class="card mt-16" v-if="step === 2">
      <div class="section-title">日期与房型</div>
      <el-form :model="form" label-width="100px">
        <el-form-item label="入住日期">
          <el-date-picker v-model="form.dateRange" value-format="yyyy-MM-dd" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="房型">
          <el-radio-group v-model="form.roomId">
            <el-radio v-for="room in storeRooms" :key="room.id" :label="room.id">{{ room.name }} ￥{{ room.price }}/天</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="附加服务">
          <el-checkbox-group v-model="form.serviceIds">
            <el-checkbox v-for="service in storeServices" :key="service.id" :label="service.id">{{ service.name }} ￥{{ service.price }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
    </div>

    <div class="card mt-16" v-if="step === 3">
      <div class="section-title">确认信息</div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="门店">{{ selectedStore.name }}</el-descriptions-item>
        <el-descriptions-item label="宠物">{{ selectedPet.name }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ form.dateRange.join(' 至 ') }}</el-descriptions-item>
        <el-descriptions-item label="房型">{{ selectedRoom.name }}</el-descriptions-item>
        <el-descriptions-item label="服务">{{ serviceNames }}</el-descriptions-item>
        <el-descriptions-item label="预计费用">￥{{ total }}</el-descriptions-item>
      </el-descriptions>
      <el-input type="textarea" v-model="form.remark" class="mt-12" placeholder="备注说明" />
    </div>

    <div class="card mt-16 flex-between">
      <el-button :disabled="step === 0" @click="step--">上一步</el-button>
      <el-button type="primary" @click="nextStep">{{ step === 3 ? '提交预约' : '下一步' }}</el-button>
    </div>
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader.vue'
import { list, createBooking, calcNights } from '@/mock'

export default {
  name: 'BookingCreate',
  components: { PageHeader },
  data() {
    return {
      step: 0,
      stores: [],
      pets: [],
      rooms: [],
      services: [],
      form: {
        storeId: null,
        petId: null,
        dateRange: [],
        roomId: null,
        serviceIds: [],
        remark: ''
      }
    }
  },
  computed: {
    storeRooms() {
      return this.rooms.filter(r => String(r.storeId) === String(this.form.storeId))
    },
    storeServices() {
      return this.services.filter(s => String(s.storeId) === String(this.form.storeId))
    },
    selectedStore() {
      return this.stores.find(s => String(s.id) === String(this.form.storeId)) || {}
    },
    selectedPet() {
      return this.pets.find(p => String(p.id) === String(this.form.petId)) || {}
    },
    selectedRoom() {
      return this.rooms.find(r => String(r.id) === String(this.form.roomId)) || {}
    },
    nights() {
      return calcNights(this.form.dateRange)
    },
    serviceNames() {
      return this.services.filter(s => this.form.serviceIds.includes(s.id)).map(s => s.name).join('、') || '无'
    },
    total() {
      const roomPrice = this.selectedRoom.price || 0
      const serviceSum = this.services.filter(s => this.form.serviceIds.includes(s.id)).reduce((sum, s) => sum + s.price, 0)
      return this.nights * roomPrice + serviceSum
    }
  },
  created() {
    this.stores = list('stores')
    this.pets = list('pets')
    this.rooms = list('rooms')
    this.services = list('services')
    if (this.$route.query.storeId) {
      this.form.storeId = Number(this.$route.query.storeId)
    }
  },
  methods: {
    nextStep() {
      if (!this.validateStep()) return
      if (this.step < 3) {
        this.step += 1
        return
      }
      const user = this.$store.getters['auth/userInfo']
      const payload = { ...this.form, userId: user ? user.id : 1 }
      const { order } = createBooking(payload)
      this.$message.success('预约已提交，请确认订单')
      this.$router.push({ path: '/c/booking/confirm', query: { orderId: order.id } })
    },
    validateStep() {
      if (this.step === 0 && !this.form.storeId) {
        this.$message.warning('请选择门店')
        return false
      }
      if (this.step === 1 && !this.form.petId) {
        this.$message.warning('请选择宠物')
        return false
      }
      if (this.step === 2) {
        if (!this.form.dateRange.length) {
          this.$message.warning('请选择日期')
          return false
        }
        if (!this.form.roomId) {
          this.$message.warning('请选择房型')
          return false
        }
      }
      return true
    }
  }
}
</script>

<style scoped>
.store-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}
.store-item {
  border: 1px solid #eef2f7;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.store-item.active {
  border-color: #3a6ff8;
  box-shadow: 0 6px 16px rgba(58, 111, 248, 0.15);
}
.store-item img {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  object-fit: cover;
}
</style>

