import { glob } from 'glob'
const viewsComponent = await import.meta.glob('/src/views/backend/**/*.vue')
console.log('viewsComponent keys:', Object.keys(viewsComponent).slice(0, 5))
