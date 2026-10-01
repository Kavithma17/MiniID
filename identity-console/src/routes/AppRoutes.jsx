import { Routes, Route } from 'react-router-dom'
import DashboardPage from '../pages/DashboardPage.jsx'
import ResourcePage from '../pages/ResourcePage.jsx'
import DetailPage from '../pages/DetailPage.jsx'
import CreatePage from '../pages/CreatePage.jsx'
import SettingsPage from '../pages/SettingsPage.jsx'

export default function AppRoutes() {
  return <Routes><Route path="/" element={<DashboardPage />} /><Route path="/users" element={<ResourcePage type="users" />} /><Route path="/users/new" element={<CreatePage kind="user" />} /><Route path="/users/:id" element={<DetailPage />} /><Route path="/roles" element={<ResourcePage type="roles" />} /><Route path="/roles/new" element={<CreatePage kind="role" />} /><Route path="/roles/:id" element={<DetailPage kind="role" />} /><Route path="/applications" element={<ResourcePage type="applications" />} /><Route path="/applications/new" element={<CreatePage kind="application" />} /><Route path="/applications/:id" element={<DetailPage kind="application" />} /><Route path="/identity-providers" element={<ResourcePage type="identity-providers" />} /><Route path="/sessions" element={<ResourcePage type="sessions" />} /><Route path="/audit" element={<ResourcePage type="audit" />} /><Route path="/organizations" element={<ResourcePage type="organizations" />} /><Route path="/settings" element={<SettingsPage />} /><Route path="*" element={<DashboardPage />} /></Routes>
}
