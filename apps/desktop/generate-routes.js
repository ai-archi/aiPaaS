import { generateRouteTree } from '@tanstack/router-plugin'

generateRouteTree({
  target: 'react',
  autoCodeSplitting: true,
  routesDirectory: './src/renderer/routes',
  generatedRouteTree: './src/renderer/routeTree.gen.ts',
}) 